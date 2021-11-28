(ns hospital-prop-based-test.core
  (:require [clojure.test.check.generators :as gen]))

(println (gen/sample gen/boolean 3))
(println (gen/sample gen/int 100))
(println (gen/sample gen/string))
(println (gen/sample gen/string-alphanumeric 100))

(println (gen/sample (gen/vector gen/int 15) 100))
(println (gen/sample (gen/vector gen/int 1 5) 100))
(println (gen/sample (gen/vector gen/int) 100))