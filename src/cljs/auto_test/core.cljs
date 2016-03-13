(ns auto-test.core
    (:require [reagent.core :as r :refer [atom]]
              ;;              [reagent.session :as session]
              [secretary.core :as secretary :include-macros true]
              [accountant.core :as accountant]
              cljsjs.react-autosuggest
              ;; [cljsjs.react :as react]
              ;; [cljsjs.react.dom :as react-dom]
              ;
              
              [clojure.string :as str]
              ))

(def languages [
  (js-obj "name" "C,"   "year" 1972)
  (js-obj "name" "C#" "year" 2000)
  (js-obj "name" "C++" "year" 1983)
  (js-obj "name" "Clojure" "year" 2007)
  (js-obj "name" "Elm" "year" 2012)
  (js-obj "name" "Go" "year" 2009)
  (js-obj "name" "Haskell" "year" 1990)
  (js-obj "name" "Java" "year" 1995)
  (js-obj "name" "Javascript" "year" 1995)
  (js-obj "name" "Perl" "year" 1987)
  (js-obj "name" "PHP" "year" 1995)
  (js-obj "name" "Python" "year" 1991)
  (js-obj "name" "Ruby" "year" 1995)
  (js-obj "name" "Scala" "year" 2003)])

(defn getSuggestions [val]
  (let [escapedValue (if (string? val) (str/trim val) "")]
    (if (empty? escapedValue)
      #js []
      (let [rpatt (re-pattern (str "(?i)^" val ".*"))]
        (clj->js (filter (comp #(re-matches rpatt %) #(.-name %)) languages))))))

(defn getSuggestionValue [suggestion]
  (.-name suggestion))

(defn  renderSuggestion [suggestion]
  (.log js/console "render suggestion: ")
  (.log js/console suggestion)
  (r/reactify-component
   [:span (.-name suggestion)]))


(def Autosuggest (r/adapt-react-class js/Autosuggest))


(defn auto-suggest [id]
  (let [suggestions (r/atom (getSuggestions ""))
        as-val (r/atom "")
        update-suggestions (fn [arg]
                             (let [new-sugg (getSuggestions (.-value arg))]
                               ;;                               (.log js/console arg)
                               (reset! suggestions new-sugg)
                               nil))
        update-state-val (fn [evt new-val method]                           
                           (let [nv (.. evt -target -value)]                             
                             (reset! as-val nv)
                             nil))]
    (r/create-class
     {:display-name "autosuggest-wrapper"
      :getInitialState (constantly #js {:value "" :suggestions (getSuggestions "")})
      :childContextTypes #js {"store" js/React.PropTypes.object}
;;      :getChildContext (fn [] #js {"store" (js/Redux.createStore  store-reducer  {})})
      :reagent-render (fn [id]
                        [Autosuggest {:id id                  
                                      :suggestions @suggestions
                                      :onSuggestionsUpdateRequested update-suggestions
                                      :getSuggestionValue getSuggestionValue
                                      :renderSuggestion renderSuggestion
                                      :inputProps {:placeholder "Type 'c'"
                                                   :value @as-val
                                                   :onChange update-state-val} }])
      })))


;; -------------------------
;; Views

(def session (atom {}))

(defn home-page []
  [:div [:h2 "Welcome to auto-test"]
   [:div [auto-suggest "my-auto"]]
   [:div [:a {:href "/about"} "go to about page"]]])

(defn about-page []
  [:div [:h2 "About auto-test"]
   [:div [:a {:href "/"} "go to the home page"]]])

(defn current-page []
  [:div [(:current-page @session)]])

;; -------------------------
;; Routes

(secretary/defroute "/" []
  (swap! session assoc  :current-page #'home-page))

(secretary/defroute "/about" []
  (swap! session assoc :current-page #'about-page))

;; -------------------------
;; Initialize app

(defn mount-root []
  (r/render [current-page] (.getElementById js/document "app")))

(defn init! []
  (accountant/configure-navigation!
    {:nav-handler
     (fn [path]
       (secretary/dispatch! path))
     :path-exists?
     (fn [path]
       (secretary/locate-route path))})
  (accountant/dispatch-current!)
  (mount-root))
