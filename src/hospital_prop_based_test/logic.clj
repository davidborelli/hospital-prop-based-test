(ns hospital_prop_based_test.logic
  (:require [hospital_prop_based_test.model :as ht.model]
            [schema.core :as s]))

(defn cabe-na-fila?
  [hospital, departamento]
  (some-> hospital
          departamento
          count
          (< 5)))

(defn chega-em
  [hospital, departamento, pessoa]
  (if (cabe-na-fila? hospital departamento)
    (update hospital departamento conj pessoa)
    (throw (ex-info "Não cabe ninguém neste departamento" {:paciente pessoa}))))

(s/defn atende :- ht.model/Hospital
  [hospital :- ht.model/Hospital, departamento :- s/Keyword]
  (update hospital departamento pop))

(s/defn proxima :- ht.model/PacienteID
  "Retorna o próximo paciente da fila"
  [hospital :- ht.model/Hospital, departamento :- s/Keyword]
  (-> hospital
      departamento
      peek))

(defn mesmo-tamanho?
  [hospital-entrada, hospital-saida, de, para]
  (= (+ (count (get hospital-saida de)) (count (get hospital-saida para)))
     (+ (count (get hospital-entrada de)) (count (get hospital-entrada para)))))

(s/defn transfere :- ht.model/Hospital
  "Transfere o próximo paciente da fila De para a fila Para"
  [hospital :- ht.model/Hospital, de :- s/Keyword, para :- s/Keyword]
  {:pre  [(contains? hospital de), (contains? hospital para)]
   :post [(mesmo-tamanho? hospital % de para)]}
  (let [pessoa (proxima hospital de)]
    (-> hospital
        (atende,,, de)
        (chega-em,,, para pessoa))))














