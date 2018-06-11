(ns chapter1.data
  (:require [clojure.java.io :as io]
            [incanter.core :as i]
            [incanter.excel :as xls]))

(defmulti load-data identity)

(defmethod load-data :uk 
  [_]
  (xls/read-xls (str (io/resource "UK2010.xls")))
)

(defmethod load-data :uk-scrubbed
  [_]
  (->> (load-data :uk)
      (i/$where {"Election Year" {:$ne nil}})
  )
)


