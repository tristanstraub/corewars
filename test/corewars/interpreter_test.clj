(ns corewars.interpreter-test
  (:require [corewars.interpreter :as interpreter]
            [clojure.test :refer [deftest is]]))

(deftest machine-step-test
  (is (= '(0 553668607 302006270 1090523134 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0)
         (take 20 (:memory (interpreter/machine-step interpreter/machine)))))
  (is (= '(4 553668607 302006270 1090523134 3 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0)
         (take 20 (:memory (interpreter/machine-step (interpreter/machine-step (interpreter/machine-step interpreter/machine)))))))
  (is (= '(4 553668607 302006270 1090523134 3 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0)
         (take 20 (:memory (interpreter/machine-step (interpreter/machine-step (interpreter/machine-step (interpreter/machine-step interpreter/machine)))))))))

