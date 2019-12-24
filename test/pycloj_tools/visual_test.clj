(ns pycloj-tools.visual-test
  (:require [notespace.v0.note :refer [note note-void note-as-hiccup note-test render-this-ns!]]))

(note-void
 (require '[pycloj-tools.visual :refer :all]
          '[pycloj-tools.pyutils :as pyutils]
          '[libpython-clj.python :as py]))

(note-as-hiccup :matplotlib
 (matplotlib->svg
  (fn []
    (plt/plot (->> (repeatedly 99 rand)
                   (map (partial + -0.5))
                   (reductions +)
                   vec))
    (plt/ylabel "some numbers"))))

(render-this-ns!)
