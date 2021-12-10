(ns hospital_prop_based_test.logic-test
  (:use clojure.pprint)
  (:require [clojure.test :refer :all]
            [hospital_prop_based_test.logic :refer :all]
            [hospital_prop_based_test.model :as ht.model]
            [clojure.test.check.clojure-test :refer (defspec)]
            [clojure.test.check.generators :as gen]
            [clojure.test.check.properties :as prop]
            [schema.core :as s]))

(s/set-fn-validation! true)

; São testes baseados em exemplos
(deftest cabe-na-fila?-test
  (testing "Que cabe na fila"
    (is (cabe-na-fila? {:espera []}, :espera)))

  (testing "Que cabe pessoas em filas de tamanho até 4 inclusive"
    (doseq [fila (gen/sample (gen/vector gen/string-alphanumeric 0 4) 100)]
      (is (cabe-na-fila? {:espera fila}, :espera))))

  (testing "Que não cabe na fila quando a fila está cheia"
    (is (not (cabe-na-fila? {:espera [1 2 3 4 5]}, :espera))))

  (testing "Que não cabe na fila quando a fila está cheia"
    (is (not (cabe-na-fila? {:espera [1 2 3 4 5 6]}, :espera))))

  (testing "Que cabe na fila quando tem gente mas não está cheia"
    (is (cabe-na-fila? {:espera [1 2 3 4]}, :espera))
    (is (cabe-na-fila? {:espera [1 2]}, :espera)))

  (testing "Que não cabe quando departamento não existe"
    (is (not (cabe-na-fila? {:espera [1 2 3 4]}, :raio-x)))))

; doseq está gerando uma multiplicação de casos, fila x pessoas
;(deftest chega-em-test
;  (testing "Que é colocada uma pessoa em filas menores que 5"
;    (doseq [fila (gen/sample (gen/vector gen/string-alphanumeric 0 4) 10)
;            pessoa (gen/sample gen/string-alphanumeric 5)]
;      (println pessoa fila)
;      (is (= 1 1))))) ;mostrar que são 50 asserts (10*5)

(defspec coloca-uma-pessoa-em-filas-menores-que-5 100
         (prop/for-all
           [fila (gen/vector gen/string-alphanumeric 0 4)
            pessoa gen/string-alphanumeric]
           (is (= {:espera (conj fila pessoa)}
                  (chega-em {:espera fila} :espera pessoa)))))
(def nome-aleatorio
  (gen/fmap clojure.string/join
            (gen/vector gen/char-alphanumeric 5 10)))

(defn transforma-vetor-em-fila
  [vetor]
  (reduce conj ht.model/fila-vazia vetor))

(def fila-nao-cheia-gen
  (gen/fmap transforma-vetor-em-fila
            (gen/vector nome-aleatorio 0 4)))

(defn transfere-ignorando-erro [hospital para]
  (try
    (transfere hospital :espera para)
    (catch IllegalStateException e
      hospital)))

; Abordagem razoável porém  horrível, uma vez que é usado o tipo d o tipo do tipo
; para fazer uma cond e pegar a exception que queremos
; uma alternativa seria usar biblioteca como a carth-data
; LOD AND RETHROW
;(defn transfere-ignorando-erro [hospital para]
;  (try
;    (transfere hospital :espera para)
;    (catch clojure.lang.ExceptionInfo e
;      (cond
;        (= :fila-cheia (:type (ex-data e))) hospital
;        :else (throw e))
;      ;hospital)))

;(gen/fmap transforma-vetor-em-fila (gen/vector nome-aleatorio 0 50))
(defspec transfere-tem-que-manter-a-quantidade-de-pessoas 1
         (prop/for-all
           [
            ;espera gen/string-alphanumeric
            espera (gen/fmap transforma-vetor-em-fila (gen/vector nome-aleatorio))
            raio-x fila-nao-cheia-gen
            ultrasom fila-nao-cheia-gen
            vai-para (gen/vector (gen/elements [:raio-x :ultrasom]) 0 50)
            ]
           (let [hospital-inicial {:espera espera, :raio-x raio-x, :ultrasom ultrasom}
                 hospital-final (reduce transfere-ignorando-erro hospital-inicial vai-para)]
             ;(println (count (get hospital-final :raio-x)))
             (= (total-de-pacientes hospital-inicial)
                (total-de-pacientes hospital-final)))))