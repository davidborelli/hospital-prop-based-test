(ns hospital_prop_based_test.logic-test
  (:use clojure.pprint)
  (:require [clojure.test :refer :all]
            [hospital_prop_based_test.logic :refer :all]
            [hospital_prop_based_test.model :as ht.model]
            [schema.core :as s]))

(s/set-fn-validation! true)

(deftest cabe-na-fila?-test
  (testing "Que cabe na fila"
    (is (cabe-na-fila? {:espera []}, :espera)))

  (testing "Que não cabe na fila quando a fila está cheia"
    (is (not (cabe-na-fila? {:espera [1 2 3 4 5]}, :espera))))

  (testing "Que não cabe na fila quando a fila está cheia"
    (is (not (cabe-na-fila? {:espera [1 2 3 4 5 6]}, :espera))))

  (testing "Que cabe na fila quando tem gente mas não está cheia"
    (is (cabe-na-fila? {:espera [1 2 3 4]}, :espera))
    (is (cabe-na-fila? {:espera [1 2]}, :espera)))

  (testing "Que não cabe quando departamento não existe"
    (is (not (cabe-na-fila? {:espera [1 2 3 4]}, :raio-x)))))