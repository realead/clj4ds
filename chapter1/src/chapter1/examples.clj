(ns chapter1.examples
  (:require [chapter1.data :refer :all]
            [chapter1.mystats :as ms]
            [clojure.java.io :as io]
            [incanter.core :as i]
            [incanter.excel :as xls]
            [incanter.stats :as s]
            [incanter.charts :as c]
            [incanter.distributions :as d])
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


(defn ex-1-15
  []
  (println "show uniform distribution")
  (let [xs (take 1000 (repeatedly rand))]
      (i/view (c/histogram xs 
                   :x-label "uniform distribution"
                   :nbins 20
      ))
  )
)


(defn ex-1-16
  []
  (println "show central limit theorem")
  (let [xs (->> (repeatedly rand)
                (partition 10)
                (map s/mean)
                (take 1000)
            )]
      (i/view (c/histogram xs 
                   :x-label "distribution of means"
                   :nbins 20
      ))
  )
)



(defn ex-1-17
  []
  (println "plotting normal distribution")
  (let [dist (d/normal-distribution)
        xs (->> (repeatedly #(d/draw dist))
                (take 1000)
            )]
      (i/view (c/histogram xs 
                   :x-label "normal distribution"
                   :nbins 20
      ))
  )
)


(defn honest-baker 
 [mean sd]
 (let [dist (d/normal-distribution mean sd)]
    (repeatedly #(d/draw dist))
 )
)


(defn ex-1-18
[]
  (-> (take 1000 (honest-baker 1000 30))
      (c/histogram :x-label "Honest baker"
                   :nbins 25
      )
      (i/view)
  )
)


(defn dishonest-baker 
 [mean sd]
 (let [dist (d/normal-distribution mean sd)]
    (->> (repeatedly #(d/draw dist))
         (partition 13)
         (map #(apply max %))
    )
 )
)


(defn ex-1-19
[]
  (-> (take 1000 (dishonest-baker 1000 30))
      (c/histogram :x-label "Dishonest baker"
                   :nbins 25
      )
      (i/view)
  )
)


(defn ex-1-20
 []
 (let [ws (take 1000 (honest-baker 950 30))]
    {:mean (s/mean ws)
     :median (s/median ws)
     :skewness (s/skewness ws)
    }
  )
)

;;; Q-Q-plots

(defn ex-1-21
 []
 (->> (honest-baker 1000 30)
      (take 10000)
      (c/qq-plot)
      (i/view)
 )
 (->> (dishonest-baker 950 30)
      (take 10000)
      (c/qq-plot)
      (i/view)
 )
)

(defn ex-1-22 
  []
  (-> (c/box-plot (->> (honest-baker 1000 30)
                       (take 10000)
                  )
                  :legend true
                  :y-label "Loaf weight (g)"
                  :series-label "Honest  backer"
      )
      (c/add-box-plot (->> (dishonest-baker 950 30)
                           (take 10000)
                      )
                      :series-label "Dishonest baker"
      )
      (i/view)
  )
)


(defn ex-1-23
 []
 (let [sample-honest (->>(honest-baker 1000 30)
                         (take 1000)
                     )
       sample-dishonest (->>(dishonest-baker 950 30)
                            (take 1000)
                        )
       ecdf-honest (s/cdf-empirical sample-honest)
       ecdf-dishonest (s/cdf-empirical sample-dishonest)]
   (-> (c/xy-plot sample-honest (map ecdf-honest sample-honest)
                  :x-label "Loaf Weight"
                  :y-label "Probability"
                  :legend true
                  :series-label "Honest baker"
       )
       (c/add-lines sample-dishonest
                    (map ecdf-dishonest sample-dishonest)
                  :series-label "Dishonest baker"
       )
       (i/view)
    )
 )
)


(defn ex-1-24
 []
 (let [electorate (->>(load-data :uk-scrubbed)
                     (i/$ "Electorate")
                  )
       ecdf (s/cdf-empirical electorate)
       fitted (s/cdf-normal electorate
                            :mean (s/mean electorate) 
                            :sd   (s/sd electorate)
              )]
       (-> (c/xy-plot electorate fitted
                      :x-label "Electorate"
                      :y-label "Probability"
                      :series-label "Fitted"
                      :legend true
           )
           (c/add-lines electorate (map ecdf electorate)
                      :series-label "Empirical"
           )
           (i/view)
       )
  )
)


(defn ex-1-25
 []
 (->> (load-data :uk-scrubbed)
      (i/$ "Electorate")
      (c/qq-plot)
      (i/view)
 )
)



