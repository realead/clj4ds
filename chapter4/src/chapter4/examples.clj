(ns chapter4.examples
  (:require [chapter4.data :refer :all]
            [clojure.java.io :as io]
            [incanter.core :as i]
            [incanter.excel :as xls]
            [incanter.stats :as s]
            [incanter.charts :as c]
            [incanter.distributions :as d]
            [clj-time.core :as time]
            [clj-time.format :as f]
            [clj-time.predicates :as p]
            [clj-time.coerce :as coerce]
  )
)


(defn ex-4-1
  [data]
  (i/view data)
)

