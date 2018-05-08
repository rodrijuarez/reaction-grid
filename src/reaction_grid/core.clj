(ns reaction-grid.core
  (:require [quil.core :as q]
            [quil.middleware :as m]))

(defn setup []
  (q/frame-rate 30)
  (q/color-mode :hsb)
  {})

(defn update-state [state])

(defn draw-state [state])

(q/defsketch reaction-grid
  :title "You spin my circle right round"
  :size [500 500]
  :setup setup
  ;:update update-state
  :draw draw-state
  :features [:keep-on-top]
  :middleware [m/fun-mode])
