(ns pycloj-tools.pyutils-test
  (:require [notespace.v0.note :refer [note note-void note-md note-test render-this-ns!]]
            [libpython-clj.python :as py]))

(note-void
 (require '[pycloj-tools.pyutils :refer :all]
          '[pycloj-tools.browser :as browser]
          '[libpython-clj.python :as py]))

(note-test
 :maybe-get-attr-test
 [[=
   (-> "dummy_package.module_a.submodule_aa.submodule_aaa"
       browser/name->module
       (maybe-get-attr  "__path__"))
   (-> "dummy_package.module_a.submodule_aa.submodule_aaa"
       browser/name->module
       (py/get-attr  "__path__"))]
  [nil?
   (-> "dummy_package.module_a.submodule_aa.submodule_aab"
       browser/name->module
       (maybe-get-attr  "__path__"))]])

(render-this-ns!)
