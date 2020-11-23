(ns corewars.interpreter
  (:require [corewars.decoder :as decoder]))

(defn machine-next
  [machine]
  (update machine :ptr (fn [ptr] (mod (inc ptr) (count (:memory machine))))))

(def *machine-store-hook*
  (atom nil))

(defn machine-store
  [machine value addr]
  (let [size (count (:memory machine))]
    (-> machine
        (cond->
            @*machine-store-hook*
          (@*machine-store-hook* (mod addr size) value))
        (update :memory assoc (mod addr size) value))))

(defn machine-get
  [machine addr]
  (let [size (count (:memory machine))]
    (get-in machine [:memory (mod addr size)])))

(defn machine-addr
  [machine [addr-type offset]]
  (let [base (+ (:ptr machine) offset)]
    (case addr-type
      :relative base
      :indirect (+ base (machine-get machine base)))))

(defn machine-load
  [machine [value-type value :as op]]
  (case value-type
    :immediate value
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
  [machine [_ _ op]]
  (assoc machine :ptr (machine-addr machine op)))

(defn machine-step
  [machine]
  (as-> machine machine
    (assoc machine :ptr (first (:ptrs machine)))
    (machine-eval machine (-> machine
                              (machine-get (:ptr machine))
                              (decoder/disassemble-1)))
    (update machine :ptrs (fn [ptrs]
                            (let [[_ & ptrs] ptrs]
                              (conj (vec ptrs) (:ptr machine)))))))

#_(last (take 2 (iterate machine-step machine)))



