(ns  chapter1.mystats-test
  (:require [clojure.test :refer :all]
            [chapter1.mystats :as ms]))

(deftest test-mean
  (testing "mean"
    (is (ms/mean [0 1 2]) 1)
  )
)


(deftest test-median-odd
  (testing "median odd"
    (is (ms/median [0 1 2]) 1)
  )
)

(deftest test-median-even
  (testing "median even"
    (is (ms/median [0 2 4 5]) 3)
  )
)


(deftest test-variance-one
  (testing "variance of one element is zero"
    (is (ms/variance [6]) 0)
  )
)

(deftest test-variance
  (testing "variance"
    (is (ms/variance [0 4]) 4)
  )
)


(deftest test-sd
  (testing "standard-deviation"
    (is (ms/standard-deviation [0 4]) 2)
  )
)

(deftest test-quantile-0
  (testing "quatile"
    (is (ms/quantile [0 1 2 4] 0) 0)
  )
)

(deftest test-quantile-0.25
  (testing "quatile"
    (is (ms/quantile [0 1 2 4] 0.25) 1)
  )
)

(deftest test-quantile-0.5
  (testing "quatile"
    (is (ms/quantile [0 1 2 4] 0.75) 2)
  )
)

(deftest test-quantile-1
  (testing "quatile"
    (is (ms/quantile [0 1 2 4] 1) 4)
  )
)





