(ns pycloj-tools.visual-test
  (:require [pycloj-tools.visual :refer :all]
            [pycloj-tools.pyutils :as pyutils]
            [notespace.note :refer [note note-as-hiccup note-test render-this-ns!]]
            [libpython-clj.python :as py]))

(note-as-hiccup :matplotlib
 (matplotlib->svg
  (fn []
    (plt/plot (->> (repeatedly 99 rand)
                   (map (partial + -0.5))
                   (reductions +)
                   vec))
    (plt/ylabel "some numbers"))))

(render-this-ns!)
