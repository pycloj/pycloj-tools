(ns pycloj-tools.pyutils
  (:require [libpython-clj.python :as py]))

(defn maybe-get-attr [item item-name]
  (when (py/has-attr? item item-name)
    (py/get-attr item item-name)))

