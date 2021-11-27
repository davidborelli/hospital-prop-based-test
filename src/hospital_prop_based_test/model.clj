(ns hospital_prop_based_test.model
  (:require [schema.core :as s]))

(def fila-vazia clojure.lang.PersistentQueue/EMPTY)

(defn novo-hospital []
  {:espera       fila-vazia
   :laboratorio1 fila-vazia
   :laboratorio2 fila-vazia
   :laboratorio3 fila-vazia})

(s/def PacienteID s/Str)
(s/def Departamento (s/queue PacienteID))
(s/def Hospital {s/Keyword Departamento})

; apenas para teste
;(s/validate PacienteID "David")
;(s/validate PacienteID 14)
;(s/validate Departamento ["Dep1", "Dep2"])
;(s/validate Hospital {:espera ["David", "Erick"]})