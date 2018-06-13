(ns chapter1.examples
  (:require [chapter1.data :refer :all]
            [chapter1.mystats :as ms]
            [clojure.java.io :as io]
            [incanter.core :as i]
            [incanter.excel :as xls]
            [incanter.stats :as s]
            [incanter.charts :as c])
)

(defn ex-1-1
  []
  (println "name colums of uk-data set:\n")
  (i/col-names (load-data :uk))
)

(defn ex-1-3
  []
  (println "distinct values in Election Year:")
  (distinct (i/$ "Election Year" (load-data :uk)))
)

(defn ex-1-4
   []
   (println "frequencies of values in Election Year:")
   (->> (load-data :uk)
        (i/$ "Election Year")
        (frequencies)
   )
)

(defn ex-1-5
  []
  (println "values in the nil-row:")
  (->> (load-data :uk)
       (i/$where {"Election Year" {:$eq nil}})
       (i/to-map)
  )
)

(defn ex-1-6
 [] 
 (println "counting electorate:")
 (->> (load-data :uk-scrubbed)
      (i/$ "Electorate")
      (count)
 )
)

(defn ex-1-7
  []
  (println "average electorate:")
  (->> (load-data :uk-scrubbed)
       (i/$ "Electorate")
       (s/mean)
  )
)

(defn ex-1-8
  []
  (println "median electorate:")
  (->> (load-data :uk-scrubbed)
       (i/$ "Electorate")
       (s/median)
  )
)


(defn ex-1-9
  []
  (println "standard deviation:")
  (->> (load-data :uk-scrubbed)
       (i/$ "Electorate")
       (s/sd)
  )
)


(defn ex-1-11
  []
  (println "bin data")
  (->> (load-data :uk-scrubbed)
       (i/$ "Electorate")
       (ms/bin 10)
       (frequencies)
  )
)



(defn ex-1-14
  []
  (println "show histogram")
  (->  (i/$ "Electorate" (load-data :uk-scrubbed))
       (c/histogram  :nbins 20)
       (i/view)
  )
)
