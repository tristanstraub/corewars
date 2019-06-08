(ns corewars.controlpanel
  (:require [impi.core :as impi]
            [corewars.interpreter :as interpreter]
            [corewars.decoder :as decoder]
            [corewars.examples :as examples]
            [rum.core :as rum]
            [goog.dom :as dom]))

(enable-console-print!)

(defn draw-machine
  [el machine]
  (let [nx      100
        ny      80
        cw      10
        ch      10
        journal (into {} (mapv vector (reverse (take 10 (reverse (:journal machine)))) (range)))
        ptrs    (into {} (mapv vector (:ptrs machine) (range)))]
    (impi/mount
     :example-scene
     {:pixi/renderer {:pixi.renderer/size [1000 410]}
      :pixi/stage    {:impi/key                :performance
                      :pixi.object/type        :pixi.object.type/container
                      :pixi.container/children
                      [{:impi/key             :gfx
                        :pixi.object/type     :pixi.object.type/graphics
                        :pixi.graphics/shapes (concat (for [addr (range (count (:memory machine)))
                                                            :let [x (mod addr 100)
                                                                  y (int (/ addr 100))]]
                                                        (cond (not= 0 (get (:memory machine) addr))
                                                              {:impi/key            [x y]
                                                               :pixi.shape/position [(* cw x) (* ch y)]
                                                               :pixi.shape/type     :pixi.shape.type/rectangle
                                                               :pixi.shape/size     [9 9]
                                                               :pixi.shape/fill     {:pixi.fill/color (cond (ptrs addr)    0xffffff
                                                                                                            (journal addr) (+ (* (- 0xff (mod (journal addr) 0x100)) 0x000100)
                                                                                                                              (* (mod (journal addr) 0x100) 0x010000))
                                                                                                            :else          0x770000)
                                                                                     :pixi.fill/alpha 1}}

                                                              :else
                                                              {:impi/key            [x y]
                                                               :pixi.shape/position [(inc (* cw x)) (* ch y)]
                                                               :pixi.shape/type     :pixi.shape.type/rectangle
                                                               :pixi.shape/size     [9 9]
                                                               :pixi.shape/line
                                                               {:pixi.line/width 1
                                                                :pixi.line/color 0x22FF11
                                                                :pixi.line/alpha 0.5}})))}]}}
     el)))

(def impi
  {:after-render (fn [state]
                   (draw-machine (js/ReactDOM.findDOMNode (:rum/react-component state))
                                 (first (:rum/args state)))                   
                   state)})

(rum/defc frame < impi
  [machine]
  [:div])

(rum/defc debugger
  [machine]
  [:div {:style {:max-height "400px" :overflow "scroll"}}
   (for [[i line]  (map vector (range) (map decoder/field-string (decoder/disassemble (take 10 (drop (:ptr machine)
                                                                                                     (:memory machine))))))]
     [:div {:key i} line])])

(rum/defc main < rum/reactive
  [machine]
  (let [machine (rum/react machine)]
    [:div
     (frame machine)
     (debugger machine)]))

(defonce animated
  (let [machine (atom examples/machine)]
    (reset! interpreter/*machine-store-hook* (fn [machine addr value]
                                               (update machine :journal conj addr)))

    (rum/mount (main machine) (dom/getElement "app"))

    (js/requestAnimationFrame
     (fn cb []
       (try
         (swap! machine #(interpreter/machine-step (assoc % :journal nil)))
         (js/requestAnimationFrame cb)
         (catch js/Error e
           (.log js/console e)))))))


