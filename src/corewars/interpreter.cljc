(ns corewars.interpreter
  (:require [corewars.parser :as parser]
            [corewars.examples :as examples]
            #?(:cljs [cljs.reader])
            [corewars.emitter :as emitter]
            [corewars.decoder :as decoder]))

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
  (-> machine
      (machine-get (:ptr machine))
      (decoder/disassemble-1)
      (->> (machine-eval machine))))

(def machine
  {:memory  (vec (first (partition 4096 4096 (repeat 0) (emitter/assemble (parser/parse examples/imp)))))
   :ptr     0})


#_(last (iterate machine-step machine))



