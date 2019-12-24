(ns pycloj-tools.browser-test
  (:require [notespace.v0.note :refer [note note-void note-md note-as-md note-hiccup note-as-hiccup note-test render-this-ns!]]
            [clojure.string :as string]))

(note-void
 (require '[pycloj-tools.browser :refer :all]
          '[pycloj-tools.pyutils :as pyutils]
          '[libpython-clj.python :as py]
          '[clojure.java.shell :refer [sh]]
          '[clojure.string :as string]))

(note-md
 "The `pycloj-tools.browser` namespace offers a collection of functions for inspecting python modules.
We will test it with realistic python packages such as `pandas`, as well as with a dummy one, that we have here:")

(note-as-hiccup
 [:div
  (-> (sh "find" "dummy_package")
      :out
      (string/split (re-pattern "\n"))
      (->> (filter (fn [line] (not (re-find (re-pattern "__pycache__") line))))
           (map (fn [line] [:li line]))
           (into [:ul])))])

(note-test
 :name-module-test
 [[=
   (-> "dummy_package.module_a"
       name->module
       module->name)
   "dummy_package.module_a"]])

(note-test
 :failed-import-test
 [[=
   (-> "dummy_package.modulllle_a"
       name->module)
   {:failed-import? true,
    :module-name    "dummy_package.modulllle_a",
    :cause          "ModuleNotFoundError: No module named 'dummy_package.modulllle_a'\n"}]])

(note-test
 :submodule-names-test
 [[= (-> "dummy_package.module_a"
         name->module
         module->submodules-names)
   ["dummy_package.module_a.submodule_aa"]]
  [= (-> "pandas"
         name->module
         module->submodules-names)
   ["pandas._config" "pandas._libs" "pandas.api" "pandas.arrays" "pandas.compat" "pandas.core" "pandas.errors" "pandas.io" "pandas.plotting" "pandas.tests" "pandas.tseries" "pandas.util"]]])



(note-test
 :no-submodules-test
 [[= (-> "dummy_package.module_a.submodule_aa.submodule_aaa"
         name->module
         module->submodules-names)
   []]
  [=
   (-> "pandas.plotting._matplotlib"
       name->module
       module->submodules-names)
   []]])


(note-test
 :submodules-of-failed-import-test
 [[nil? (-> "dummy_package.modulllle_a"
            name->module ;; failed import
            module->submodules-names)]])


(note-test
 :submodules-test
 [[= (->> "dummy_package.module_a"
          name->module
          module->submodules
          (map module->name))
   ["dummy_package.module_a.submodule_aa"]]])


(note-test
 :recursive-submodules-test
 [[= (->> "dummy_package.module_a"
          name->module
          module->recursive-submodules
          (map module->name))
   ["dummy_package.module_a"
    "dummy_package.module_a.submodule_aa"
    "dummy_package.module_a.submodule_aa.submodule_aaa"]]])





(render-this-ns!)
