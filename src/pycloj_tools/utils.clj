(ns pycloj-tools.utils
  (:require [com.rpl.specter :refer [transform MAP-VALS]]))

(defmacro def+
  "binding => binding-form
  internalizes binding-forms as if by def.
  See http://clojuredocs.org/clojure.core/destructure ."
  [& bindings]
  (let [bings (partition 2 (destructure bindings))]
    (sequence cat
              ['(do)
               (map (fn [[var value]] `(def ~var ~value)) bings)
               [(mapv (fn [[var _]] (str var)) bings)]])))

(defn fmap [f m]
  (transform [MAP-VALS] f m))
