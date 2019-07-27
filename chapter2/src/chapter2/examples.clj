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

(defn load-and-group-data
   [filename]
   (->> (load-data filename)
         (:rows)
         (group-by :site)
         (map-vals (partial map :dwell-time))
   )
)

(defn load_new_site_data
   []
   (load-and-group-data "new-site.tsv")
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



(defn ex-2-20
 []
 (->>  (i/transform-col (load-data "multiple-sites.tsv") :dwell-time float)
       (i/$rollup :mean :dwell-time :site)
       (i/$order :dwell-time :desc)
       (i/view)
 )
)


(defn ex-2-21
 []
 (let [data (load-and-group-data "multiple-sites.tsv") 
       alpha 0.05]
       (doseq [[site-a times-a] data
               [site-b times-b] data
               :when (> site-a site-b)
               :let [p-val (-> (s/t-test times-a :y times-b)
                                (:p-value)
                            )
                     ]
              ]
              (when (< p-val alpha)
                 (println site-b "and" site-a
                          "are significantly different:"
                          (format "%.3f" p-val)
                  )
              )
       )
 )
)

(defn ex-2-22
 []
 (let [data (load-and-group-data "multiple-sites.tsv") 
       alpha 0.05]
       (doseq [[site-a times-a] data
               :let [p-val (-> (s/t-test times-a :mu 90)
                                (:p-value)
                            )
                     ]
              ]
              (when (< p-val alpha)
                 (println site-a
                          "is significantly different:"
                          (format "%.3f" p-val)
                  )
              )
       )
 )
)

;bonferroni correcction
(defn ex-2-23
 []
 (let [data (load-and-group-data "multiple-sites.tsv") 
       alpha (/ 0.05 (count data))]
       (doseq [[site-a times-a] data
               [site-b times-b] data
               :when (> site-a site-b)
               :let [p-val (-> (s/t-test times-a :y times-b)
                                (:p-value)
                            )
                     ]
              ]
              (when (< p-val alpha)
                 (println site-b "and" site-a
                          "are significantly different:"
                          (format "%.3f" p-val)
                  )
              )
       )
 )
)


(defn ssw 
  [groups]
  (->> (map s/sum-of-square-devs-from-mean groups)
       (reduce +)
  ) 
)


(defn sst 
  [groups]
  (let [data (apply concat groups)]
       (s/sum-of-square-devs-from-mean (vec data))
  )
)

(defn ssb
  [groups]
  (- (sst groups)
     (ssw groups)
  )
)

(defn f-stat 
  [groups df1 df2]
  (let [msb (/ (ssb groups) df1)
        msw (/ (ssw groups) df2)]
     (/ msb msw)
  )
)

(defn f-test 
 [groups]
 (let [n (count (apply concat groups))
       m (count groups)
       df1 (- m 1)
       df2 (- n m)
       f-stat (f-stat groups df1 df2)]
    (s/cdf-f f-stat :df1 df1 :df2 df2 :lower-tail? false) 
 )
)


;f-test
(defn ex-2-24
 []
 (let [data (load-and-group-data "multiple-sites.tsv")
       grouped (into [] (for [[k v] data] v))]
      (f-test grouped)
 )
)

