(ns  chapter1.mystats-test
  (:require [clojure.test :refer :all]
            [chapter1.mystats :as ms]))

(deftest a-test
  (testing "mean"
    (is (ms/mean [0 1 2]) 1)
  )
)
