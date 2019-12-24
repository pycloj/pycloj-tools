(ns pycloj-tools.visual
  (:require [libpython-clj.require :refer [require-python]])
  (:import [java.io File]))

(require-python '[matplotlib.pyplot :as plt])

(defn matplotlib->svg [f]
  (let [file (File/createTempFile "matplotlib" ".svg")
        path (.getAbsolutePath file)]
    (plt/figure)
    (f)
    (plt/savefig path)
    (let [result (slurp path)]
      (.delete file)
      result)))
