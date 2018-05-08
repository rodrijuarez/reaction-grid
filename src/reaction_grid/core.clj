(ns reaction-grid.core
  (:require [quil.core :as q]
            [quil.middleware :as m]))

(def shape-size 50)
(def tile-count 10)
;(def shape-color (q/color 0 130 164))

(defn setup []
  (q/frame-rate 30)
  (q/color-mode :hsb)
  {:tile-count tile-count
   :tile-height (/ (q/height) tile-count)
   :tile-width (/ (q/width) tile-count)
   :current-shape (q/load-shape "module_7.svg")
   :shape-angle 0
   :fill-mode 0
   :size-mode 0
   :new-shape-size shape-size})

;(defn update-state [state])

(defn calc-pos-x [x tile-width]
  (+ (* tile-width x) (/ tile-width 2)))

(defn calc-pos-y [y tile-height tile-width]
  (+ (* tile-height y) (/ tile-width 2)))

(defn distance-from-mouse-y [y]
  (- (q/mouse-y) y))

(defn distance-from-mouse-x [x]
  (- (q/mouse-x) x))

(defn calc-angle [x y shape-angle]
  (+ (q/atan2 (distance-from-mouse-y y) (distance-from-mouse-x x)) (q/radians shape-angle)))

(defn produce-tiles [tile-count tile-width tile-height shape-angle]
  (for [y (range tile-count)
        x (range tile-count)] (let [pos-x (calc-pos-x x tile-width)
                                    pos-y (calc-pos-y y tile-height tile-width)
                                    angle (calc-angle pos-x pos-y shape-angle)] [pos-x pos-y angle])))

(defn draw-shapes [new-shape-size current-shape shape-angle pos-x pos-y angle]
  (.enableStyle current-shape)
  (q/push-matrix)
  (q/translate pos-x pos-y)
  (q/rotate angle)
  (q/shape-mode :center)
  (q/no-stroke)
  (q/shape current-shape 0 0 new-shape-size new-shape-size)
  (q/pop-matrix))

(defn draw-state [state]
  (q/background 255)
  (q/smooth)
  (println "tile-count" tile-count)
  (let [{:keys [new-shape-size current-shape shape-angle tile-count tile-width tile-height]} state]
    (doall (map #(apply draw-shapes new-shape-size current-shape shape-angle %) (produce-tiles tile-count tile-width tile-height shape-angle)))))

(defn key-released [a b]
  (case (str (q/raw-key))
    ("c" "C") (assoc (q/state) :fill-mode (mod (+ (:fill-mode q/state) 1) 4))
    ("d" "D") (assoc (q/state) :size-mode (mod (+ (:size-mode q/state) 1) 3))
    ("g" "G") (do
                (assoc (q/state) :tile-count (+ (:tile-count (q/state)) 5))
                (when (> (:tile-count (q/state)) 20)
                  (assoc (q/state) :tile-count 10))
                (assoc (q/state) :tile-width (/ (q/width) (float (:tile-count (q/state)))))
                (assoc (q/state) :tile-height (/ (q/height) (float (:tile-count (q/state))))))
    ("1") (assoc (q/state) :current-shape (q/load-shape "module_1.svg"))
    ("2") (assoc (q/state) :current-shape (q/load-shape "module_2.svg"))
    ("3") (assoc (q/state) :current-shape (q/load-shape "module_3.svg"))
    ("4") (assoc (q/state) :current-shape (q/load-shape "module_4.svg"))
    ("5") (assoc (q/state) :current-shape (q/load-shape "module_5.svg"))
    ("6") (assoc (q/state) :current-shape (q/load-shape "module_6.svg"))
    ("7") (assoc (q/state) :current-shape (q/load-shape "module_7.svg"))
    nil))

;(defn key-pressed [a b]
  ;(case (q/key-as-keyword)
    ;:up (assoc (q/state) :shape-size (+ (:shape-size (q/state)) 5))
    ;:down (assoc (q/state) :shape-size (max (- @(:shape-size (q/state)) 5) 5))
    ;:left (assoc (q/state) :shape-angle (- (:shape-angle (q/state)) 5))
    ;:right (assoc (q/state) :shape-angle (+ (:shape-angle (q/state)) 5))
    ;nil))

(q/defsketch reaction-grid
  :title "You spin my circle right round"
  :size [600 600]
  :setup setup
  ;:update update-state
  :draw draw-state
  ;:key-pressed key-pressed
  :key-released key-released
  :features [:keep-on-top]
  :middleware [m/fun-mode])
