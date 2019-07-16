(ns chapter2.examples
  (:require [chapter2.data :refer :all]
            [clojure.java.io :as io]
            [incanter.core :as i]
            [incanter.excel :as xls]
            [incanter.stats :as s]
            [incanter.charts :as c]
            [incanter.distributions :as d]
            [clj-time.format :as f]
            [clj-time.predicates :as p]
  )
)

(defn ex-2-1
  []
  (-> (load-data "dwell-times.tsv")
      (i/view)
  )
)



(defn ex-2-2
  []
  (-> (i/$ :dwell-time (load-data "dwell-times.tsv"))
      (c/histogram :x-label "Dwell time (s)" :nbins 50)
      (i/view)
  )
)


(defn ex-2-3
  []
  (-> (i/$ :dwell-time (load-data "dwell-times.tsv"))
      (c/histogram :x-label "Dwell time (s)" :nbins 50)
      (c/set-axis :y (c/log-axis :label "Log Frequency"))
      (i/view)
  )
)


(defn ex-2-4
  []
  (let  [data (i/$ :dwell-time (load-data "dwell-times.tsv"))]
      (println "Mean:" (s/mean data))
      (println "Median:" (s/median data))
      (println "SD:" (s/sd data))
  )
)


(defn ex-2-5
  []
  (let  [data (->> (load-data "dwell-times.tsv")
                   (daily-mean-dwell-times)
                   (i/$ :dwell-time)
              )]
      (println "Mean:" (s/mean data))
      (println "Median:" (s/median data))
      (println "SD:" (s/sd data))
  )
)



(defn ex-2-6
  []
  (let  [data (->> (load-data "dwell-times.tsv")
                   (daily-mean-dwell-times)
                   (i/$ :dwell-time)
              )]
      (-> (c/histogram data 
                   :x-label "Daily mean dwelltime"
                   :nbins 20)
           (i/view)
      )
  )
)

(defn ex-2-7
  []
  (let  [data (->> (load-data "dwell-times.tsv")
                   (daily-mean-dwell-times)
                   (i/$ :dwell-time)
              )
         mean (s/mean data)
         sd   (s/sd data)
         pdf  (fn [x] (s/pdf-normal x :mean mean :sd sd))]
      (-> (c/histogram data 
                   :x-label "Daily mean dwelltime"
                   :nbins 20
                   :density true)
           (c/add-function pdf 80 100)
           (i/view)
      )
  )
)

(defn standard-error
  [data]
  (/ (s/sd data) (Math/sqrt (count data)))
)

(defn ex-2-8
  [data]
  (let  [may-1 (f/parse-local-date "2015-05-01")]
      (->> (with-parsed-date data)
           (filter-days-dwell-times may-1)
           (standard-error)
      )
  )
)


(defn confidence-interval 
  [p xs]
  (let [x-bar (s/mean xs)
        se (standard-error xs)
        z-crit (s/quantile-normal (- 1 (/ (- 1 p) 2)))]
     [(- x-bar (* se z-crit))
      (+ x-bar (* se z-crit))]
   )
)

(defn ex-2-9
  [data]
  (let  [may-1 (f/parse-local-date "2015-05-01")]
      (->> (with-parsed-date data)
           (filter-days-dwell-times may-1)
           (confidence-interval 0.95)
      )
  )
)


(defn ex-2-1011
  []
  (let  [times (->> (load-data "campaign-sample.tsv")
                   (i/$ :dwell-time))]
      (println "n:    " (count times))
      (println "Mean: " (s/mean times))
      (println "Media:" (s/median times))
      (println "SD:   " (s/sd times))
      (println "SE:   " (standard-error times))
      (println "Interval" (confidence-interval 0.95 times))
  )
)

(defn ex-2-12
  [data]
  (let [means (->> (with-parsed-date data)
                   (mean-dwell-times-by-date)
                   (i/$ :dwell-time)
              )
       ]
       (-> (c/histogram means 
                        :x-label "Daily mean dwell time unfiltered (s)"
                        :nbins 20
           )
           (i/view)
       )
    )
)


(defn ex-2-13
  [data]
  (let [times (->> (with-parsed-date data)
                   (i/$where {:date {:$fn p/weekend?}})
                   (i/$ :dwell-time)
              )
       ]
      (println "n:    " (count times))
      (println "Mean: " (s/mean times))
      (println "Media:" (s/median times))
      (println "SD:   " (s/sd times))
      (println "SE:   " (standard-error times))
      (println "Interval" (confidence-interval 0.95 times))
    )
)

(defn pooled-standard-error
  [a b]
  (i/sqrt (+ (/ (i/sq (s/sd a)) (count a))
             (/ (i/sq (s/sd b)) (count b))
          )
  )
)


(defn z-stat
  [a b]
  (-> (- (s/mean a)
         (s/mean b))
      (/ (pooled-standard-error a b))
  )
)

(defn z-test
   [a b]
   (s/cdf-normal (z-stat a b))
)

(defn map-vals 
  [f m]
  (into {} (for [[k v] m] [k (f v)]))
)


(defn load_new_site_data
   []
   (->> (load-data "new-site.tsv")
         (:rows)
         (group-by :site)
         (map-vals (partial map :dwell-time))
   )
)


(defn ex-2-14
 []
 (let [data (load_new_site_data)
       a (get data 0)
       b (get data 1)
      ]
      (println "a n:" (count a))
      (println "b n:" (count b))
      (println "z-stat:" (z-stat a b))
      (println "p-value" (z-test a b))
 )
)

(defn pooled-standard-error-for-t-stat
  [a b]
  (i/sqrt (+ (i/sq (standard-error a))
             (i/sq (standard-error b))
          )
  )
)

(defn t-stat
  [a b]
  (-> (- (s/mean a)
         (s/mean b))
      (/ (pooled-standard-error-for-t-stat a b))
  )
)


(defn t-test
  [a b]
  (let [df (+ (count a) (count b) -2)]
     (- 1 (s/cdf-t (i/abs (t-stat a b )) :df df))
  )
)


(defn ex-2-1516
 []
 (let [data (load_new_site_data)
       a (get data 0)
       b (get data 1)
      ]
      (println "a n:" (count a))
      (println "b n:" (count b))
      (println "t-stat:" (t-stat a b))
      (println "t-test:" (t-test a b))
 )
)



(defn ex-2-17
 []
 (let [data (load_new_site_data)
       a (get data 0)
       b (get data 1)
      ]
      (clojure.pprint/pprint (s/t-test a :y b :alternative :lower))
 )
)


(defn ex-2-18
 []
 (let [data (load_new_site_data)
       b (get data 1)
      ]
      (clojure.pprint/pprint (s/t-test b :mu 90))
 )
)


(defn ex-2-19
 []
 (let [data    (->> (load-data "new-site.tsv")
                    (i/$where {:site {:$eq 1}})
                    (i/$ :dwell-time)
               )
      ]
      (-> (s/bootstrap data s/mean :size 10000)
          (c/histogram :nbins 20
                       :x-label "Biitstrapped mean dwell times (s)")
          (i/view)
      )
 )
)

