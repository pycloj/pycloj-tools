(ns pycloj-tools.pyutils-test
  (:require [pycloj-tools.pyutils :refer :all]
            [pycloj-tools.browser :as browser]
            [notespace.note :refer [note note-md note-test render-this-ns!]]
            [libpython-clj.python :as py]))

(note-test
 :maybe-get-attr-test
 [[=
   (-> "dummy_module.submodule_a"
       browser/name->module
       (maybe-get-attr  "__path__"))
   (-> "dummy_module.submodule_a"
       browser/name->module
       (py/get-attr "__path__"))]
  [nil?
   (-> "dummy_module.submodule_a.submodule_ab"
       browser/name->module
       (maybe-get-attr  "__path__"))]])

(render-this-ns!)
