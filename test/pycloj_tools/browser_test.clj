(ns pycloj-tools.browser-test
  (:require [notespace.v0.note :refer [note note-void note-md note-test render-this-ns!]]))

(note-void
 (require '[pycloj-tools.browser :refer :all]
         '[pycloj-tools.pyutils :as pyutils]
         '[libpython-clj.python :as py]))

(note-test
 :name-module-test
 [[=
   (-> "dummy_module"
       name->module
       module->name)
   "dummy_module"]])

(note-test
 :failed-import-test
 [[=
   (-> "dummy_modulllle"
       name->module)
   {:failed-import? true,
    :module-name    "dummy_modulllle",
    :cause          "ModuleNotFoundError: No module named 'dummy_modulllle'\n"}]])

(note-test
 :submodule-names-test
 [[= (-> "dummy_module"
         name->module
         module->submodules-names)
   ["dummy_module.submodule_a"]]
  [= (-> "pandas"
         name->module
         module->submodules-names)
   ["pandas._config" "pandas._libs" "pandas.api" "pandas.arrays" "pandas.compat" "pandas.core" "pandas.errors" "pandas.io" "pandas.plotting" "pandas.tests" "pandas.tseries" "pandas.util"]]])



(note-test
 :no-submodules-test
 [[= (-> "dummy_module.submodule_a.submodule_aa"
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
 [[nil? (-> "dummy_modulllle"
            name->module ;; failed import
            module->submodules-names)]])


(note-test
 :submodules-test
 [[= (->> "dummy_module"
          name->module
          module->submodules
          (map module->name))
   ["dummy_module.submodule_a"]]])


(note-test
 :recursive-submodules-test
 [[= (->> "dummy_module"
          name->module
          module->recursive-submodules
          (map module->name))
   ["dummy_module"
    "dummy_module.submodule_a"
    "dummy_module.submodule_a.submodule_aa"]]])


(render-this-ns!)
