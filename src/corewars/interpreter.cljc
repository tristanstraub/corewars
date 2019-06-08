(ns corewars.interpreter
  (:require [corewars.parser :as parser]
            [corewars.examples :as examples]
            #?(:cljs [cljs.reader])))

(defn parse-integer
  [v]
  (int #?(:clj (read-string v)
          :cljs (cljs.reader/read-string v))))

(defn machine-next
  [machine]
  (update machine :ptr (fn [ptr] (mod (inc ptr) (count (:memory machine))))))

(defn machine-store
  [machine value addr]
  (assoc-in machine [:memory addr] value))

(defn machine-get
  [machine addr]
  (get-in machine [:memory addr]))

(defn machine-addr
  [machine [addr-type offset]]
  (let [base (+ (:ptr machine) (parse-integer (str offset)))]
    (case addr-type
      :relative base
      :indirect (+ base (machine-get machine base)))))

(defn machine-load
  [machine [value-type value :as op]]
  (case value-type
    :immediate (parse-integer (str value))
    (:relative :indirect) (machine-get machine (machine-addr machine op))))

(defmulti machine-eval (fn [machine [mnemonic & ops]] mnemonic))

(defmethod machine-eval :dat
  [machine [_ & ops]]
  (machine-next machine))

(defmethod machine-eval :add
  [machine [_ op1 op2]]
  (-> machine
      (machine-store (+ (machine-load machine op1)
                      (machine-load machine op2))
                   (machine-addr machine op2))
      (machine-next)))

(defmethod machine-eval :mov
  [machine [_ op1 op2]]
  (-> machine
      (machine-store (machine-load machine op1)
                     (machine-addr machine op2))
      (machine-next)))

(defmethod machine-eval :jmp
  [machine [_ op]]
  (assoc machine :ptr (machine-addr machine op)))

(defn machine-step
  [machine]
  (machine-eval machine (get-in machine [:instructions (:ptr machine)])))

(def machine
  {:memory       (vec (repeat 8000 0))
   :instructions (parser/parse examples/dwarf)
   :ptr          0})
