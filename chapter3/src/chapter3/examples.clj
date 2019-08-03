(ns chapter3.examples
  (:require [chapter3.data :refer :all]
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

(defn ex-3-1
  [data]
  (i/view data)
)


(defn ex-3-2
  [data]
  (-> (remove nil? (i/$ "Height, cm" data))
      (c/histogram :nbins 20
                   :x-label "height, cm"
                   :y-label "Frequency")
      (i/view)
  )
)


(defn ex-3-3
  [data]
  (-> (remove nil? (i/$ "Weight" data))
      (c/histogram :nbins 20
                   :x-label "weight, cm"
                   :y-label "Frequency")
      (i/view)
  )
)


(defn ex-3-4
  [data]
  (->>  (i/$ "Weight" data)
        (remove nil?)
        (s/skewness)
        (println "weight skewness")
  )
  (->>  (i/$ "Weight" data)
        (remove nil?)
        (i/log)
        (s/skewness)
        (println "log weight skewness")
  )
  (->>  (i/$ "Height, cm" data)
        (remove nil?)
        (s/skewness)
        (println "height skewness")
  )
)


(defn ex-3-5
  [data]
  (-> (remove nil? (i/$ "Weight" data))
      (i/log)
      (c/histogram :nbins 20
                   :x-label "weight, cm"
                   :y-label "Frequency")
      (i/view)
  )
)

(defn swimmer-data
   [data]
   (i/$where {"Height, cm" {:$ne nil} "Weight" {:$ne nil} "Sport" {:$eq "Swimming"}} data)
)


(defn ex-3-6
  [data]
  (let [swimmers (swimmer-data data)
        heights (i/$ "Height, cm" swimmers)
        weights (i/log (i/$ "Weight" swimmers))]
      (-> (c/scatter-plot heights weights
                        :x-label "Height, cm"
                        :y-label "Weight")
          (i/view)
      )
  )
)


(defn jitter 
  [limit]
  (fn [x]
     (let [amount (- (rand (* 2 limit)) limit)]
        (+ x amount)
     )
  )
)

(defn ex-3-7
  [data]
  (let [swimmers (swimmer-data data)
        heights (->> (i/$ "Height, cm" swimmers)
                     (map (jitter 0.5))
                )
        weights (->> (i/$ "Weight" swimmers)
                     (map (jitter 0.5))
                     (i/log)
                )]
      (-> (c/scatter-plot heights weights
                        :x-label "Height, cm"
                        :y-label "Weight")
          (i/view)
      )
  )
)

(defn ex-3-8
  [data]
  (let [swimmers (swimmer-data data)
        heights (i/$ "Height, cm" swimmers)
        weights (i/$ "Weight" swimmers)]
      (println "correlation" (s/correlation heights weights))
      (println "correlation log" (s/correlation heights (i/log weights)))
  )
)


; ex-3-9, for (an outline of) an explanation, why this works see: https://en.wikipedia.org/wiki/Pearson_correlation_coefficient#Testing_using_Student&#39;s_t-distribution


; under the assumption that rho is 0, the distribution of r is t-student's for df=n-2 (i.e. can be approximated by normal distribution)

(defn t-value-for-rho-zero
  [r df]
  (let [denom (- 1 (* r r))]
     (* r (i/sqrt ( / df denom)))
  )
)

(defn ex-3-9
  [data]
  (let [swimmers (swimmer-data data)
        heights (i/$ "Height, cm" swimmers)
        weights (i/$ "Weight" swimmers)
        r (s/correlation heights weights)
        df (- (count heights) 2)
        t (t-value-for-rho-zero r df)
        p-t (s/cdf-t t :df df :lower-tail? false)
        p-n (- 1 (s/cdf-normal t :mu 0 :sd 1)) ; need :lower-tail? false
        ]
      (println "t-value" t)
      (println "p-t-student" (* 2 p-t))
      (println "p-norma," (* 2 p-n))
      (println "df" df "r" r)
  )
)

; using Fisher-transformation (https://en.wikipedia.org/wiki/Fisher_transformation)
; which just a heuristic, but nontheless...

(defn critical-value 
  [confidence ntails]
  (let [lookup (- 1 (/ (- 1 confidence) ntails))]
     (s/quantile-normal lookup)
  )
)


(defn r->z
  [r]
  (* 0.5 (i/log (/ (+ 1 r) (- 1 r))))
) 

(defn z->r
  [z]
  (let [z2 (* 2 z)
        expz2 (i/exp z2)
       ]
    (/ (- expz2 1) (+ expz2 1))
  )
)

(defn r-confidence-interval
  [r n alpha]
  (let [z (r->z r)
        S (/ 1 (i/sqrt (- n 3)))
        crit (critical-value alpha 2)
        delta (* crit S)
        z_down (- z delta)
        z_up (+ z delta)
        ]
   (map z->r [z_down z_up])
  )  
)


(defn ex-3-10
  [data]
  (let [swimmers (swimmer-data data)
        heights (i/$ "Height, cm" swimmers)
        weights (i/log (i/$ "Weight" swimmers))
        r (s/correlation heights weights)]
      (println "0.95-confidence interval: " (r-confidence-interval r (count heights) 0.95))
  )
)


(defn ex-3-11
  []
  (let [c->f (fn [x] (+ 32 (* 1.8 x)))]
    (-> (c/function-plot c->f -10 40 
             :x-label "Celcius"
             :y-label "Fahrenheit")
        (i/view)
    )
  )
)

(defn slope 
  [x y]
  (/ (s/covariance x y)
     (s/variance x)
  )
)

(defn intercept 
  [x y]
  (- (s/mean y)
     (* (s/mean x) (slope x y))
  )
)

(defn swimmer_h_and_w 
   [data]
   (let [swimmers (swimmer-data data)
        heights (i/$ "Height, cm" swimmers)
        weights (i/log (i/$ "Weight" swimmers))]
      [heights weights]
   )  
)

(defn ex-3-12
  [data]
  (let [[heights weights] (swimmer_h_and_w data)
         b (slope heights weights)
         a (intercept heights weights)
       ]
       (println "Intercept: " a)
       (println "Slope: " b)
  )
)

(defn ex-3-13
  [data]
  (let [[heights weights] (swimmer_h_and_w data)
         b (slope heights weights)
         a (intercept heights weights)
         heights (map (jitter 0.5) heights)
         regression (fn [x] (+ a (* x b)))
       ]
       (-> (c/function-plot regression 150 220 
             :x-label "Height, cm"
             :y-label "log(Weight)")
           (c/add-points heights weights)
           (i/view)
       )
  )
) 

(defn ex-3-13b
  [data]
  (let [[heights weights] (swimmer_h_and_w data)
         b (slope heights weights)
         a (intercept heights weights)
         heights (map (jitter 0.5) heights)
         regression (fn [x] (+ a (* x b)))
       ]
       (-> (c/scatter-plot heights weights 
             :x-label "Height, cm"
             :y-label "log(Weight)")
           (c/add-function regression 150 220)
           (i/view)
       )
  )
) 

(defn residuals
  [x y]
  (let [ b (slope x y)
         a (intercept x y)
         estimate (fn [z] (+ a (* z b)))
         residual (fn [x y] (- y (estimate x)))]
       (map residual x y)
  )
)

(defn ex-3-14
  [data]
  (let [[heights weights] (swimmer_h_and_w data)
         res (residuals heights weights)
       ]
       (-> (c/scatter-plot heights res
             :x-label "Height, cm"
             :y-label "log(Weight)")
           (c/add-function (constantly 0) 150 220)
           (i/view)
       )
  )
) 

(defn r-squared
  [x y]
  (let [ eps (residuals x y)
         eps-var (s/variance eps)
         y-var (s/variance y)]
       (- 1 (/ eps-var y-var))
  )
)

(defn ex-3-15
  [data]
  (let [[heights weights] (swimmer_h_and_w data)]
       (r-squared heights weights)
  )
) 

;from data
(defn ex-3-16
  [data]
  (let [extracted (i/$ ["Height, cm"  "Weight"] data)
        filtered-data (i/$where {"Height, cm" {:$ne nil} "Weight" {:$ne nil}} extracted)]
     (i/to-matrix filtered-data)
  )
) 

;from sequencies
(defn ex-3-17
  [data]
  (i/matrix (remove nil? (i/$ "Height, cm" data)))
) 


(defn add-bias
  [mat]
  (i/bind-columns (repeat (i/nrow mat) 1) mat)
)

(defn normal-equation 
  [x y]
  (let [xt (i/trans x)
        xtx (i/mmult xt x)
        xty (i/mmult xt y)]
     (i/solve xtx xty)
  )
)

(defn ex-3-18
  [data]
  (let [[x y] (map i/matrix (swimmer_h_and_w data))]
       (normal-equation (add-bias x) y)
  )
) 


(defn extract-and-filter
  [data column-names]
  (let [extracted (i/$ column-names data)
        nil-filter (into {} (for [name column-names] [name {:$ne nil}]))]
        (i/$where nil-filter extracted)
  )     
)


(defn feature-matrix 
  [col-names dataset]
  (-> (extract-and-filter dataset col-names)
      (i/to-matrix)
  )
)

(defn ex-3-19
  [data]
  (feature-matrix ["Height, cm" "Age"] (swimmer-data data))
)
 

(defn ex-3-20
  [data]
  (let [filtered (->> (extract-and-filter data ["Height, cm" "Age" "Weight" "Sport"])
                      (i/$where {"Sport" {:$eq "Swimming"}})
                 )
        
        x  (-> (feature-matrix ["Height, cm" "Age"] filtered)
               (add-bias)
           )
        y  (-> (i/$ "Weight" filtered)
               (i/log)
               (i/matrix)
           )
        ]
    (normal-equation x y)
  )
)

(defn r-squared-mat 
   [x y]
   (let [coefs (normal-equation x y)
         fitted (i/mmult x coefs)
         resids (i/minus y  fitted)
         difs   (i/minus y (s/mean y))
         rss    (i/sum-of-squares resids)
         ess    (i/sum-of-squares difs)
        ]
      (- 1 (/ rss ess))
   )
)


(defn r-squared-mat-adj 
   [x y]
   (let [r-squared (r-squared-mat x y)
         n (count y)
         p (i/ncol x)
         rel (- 1 r-squared)
         adj-rel (*  rel (/ (dec n) (dec (- n p))))
        ]
      (- 1 adj-rel)
   )
)



(defn ex-3-2122
  [data]
  (let [filtered (->> (extract-and-filter data ["Height, cm" "Age" "Weight" "Sport"])
                      (i/$where {"Sport" {:$eq "Swimming"}})
                 )
        
        x  (-> (feature-matrix ["Height, cm" "Age"] filtered)
               (add-bias)
           )
        y  (-> (i/$ "Weight" filtered)
               (i/log)
               (i/matrix)
           )
        ]
    (println "r-squared" (r-squared-mat x y))
    (println "r-squared-adj" (r-squared-mat-adj x y))
  )
)

 


