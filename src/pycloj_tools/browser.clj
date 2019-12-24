(ns pycloj-tools.browser
  (:require [libpython-clj.python :as py]
            [libpython-clj.require :refer [require-python]]
            [clojure.string :as string]
            [pycloj-tools.pyutils :as pyutils]))

(require-python '([builtins :as python]
                  argparse
                  inspect
                  pkgutil
                  operator))

(def name->module
  (memoize
   (fn [module-name]
     (try (py/import-module module-name)
          (catch Exception e
            (merge
             {:failed-import? true
              :module-name    module-name}
             (-> e
                 Throwable->map
                 (select-keys [:cause]))))))))

(defn module->name [module]
  (when-not (:failed-import? module)
    (py/get-attr module "__name__")))

(defn module->submodules-names [module]
  (when-not (:failed-import? module)
    (or (some->> (pyutils/maybe-get-attr module "__path__")
                 (pkgutil/walk_packages :path)
                 (map py/->jvm)
                 (filter (fn [[_ _ ispkg]]
                           ispkg))
                 (map (fn [[_ m _]]
                        (-> module
                            module->name
                            (str "." m)))))
        ;; No __path__ -- no submodules.
        [])))


(defn module->submodules [module]
  (->> module
       module->submodules-names
       (map name->module)))


(defn module->recursive-submodules [module]
  (->> module
       module->submodules
       (mapcat module->recursive-submodules)
       (cons module)))


(defn empty->nil [v]
  (when-not (operator/eq inspect/_empty v)
    v))

(defn members->map [members]
  (->> members
       (map (fn [[k v]]
              [(keyword k) v]))
       (into {})))

(defn ->members-map
  ([object]
   (-> object
       inspect/getmembers
       members->map))
  ([object inspect-predicate]
   (-> object
       (inspect/getmembers inspect-predicate)
       members->map)))

(defn ->functions-map [object]
  (->members-map object inspect/isfunction))

(defn ->classes-map [object]
  (->members-map object inspect/isclass))

(defn protect-doctstring [dstr]
  (if (string? dstr)
    (string/replace dstr "\"" "\\\"")))

(defn function->info [f]
  (let [sig  (inspect/signature f)
        args (-> sig
                 (py/get-attr "parameters")
                 (py/call-attr "values")
                 (->>
                  (mapv (fn [arg]
                          (-> (->> [:name :empty :kind :default :annotation]
                                   (map (fn [k]
                                          [k (->> k
                                                  (py/get-attr arg)
                                                  empty->nil)]))
                                   (into {}))
                              (update :kind
                                      (comp keyword python/str))
                              (update :annotation
                                      (fn [a]
                                        (some-> a
                                                (py/get-attr "__name__")
                                                keyword))))))))]
    {:name              (py/get-attr f "__name__")
     :args              args
     :return-annotation (-> sig
                            (py/get-attr "return_annotation")
                            empty->nil)
     :doc               (protect-doctstring
                         (py/get-attr f "__doc__"))
     :generator?        (inspect/isgeneratorfunction f)
     :async?            (inspect/iscoroutine f)
     :awaitable?        (inspect/isawaitable f)
     :builtin?          (inspect/isbuiltin f)}))




