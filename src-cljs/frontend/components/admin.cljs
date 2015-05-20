(ns frontend.components.admin
  (:require [ankha.core :as ankha]
            [cljs.core.async :as async :refer [>! <! alts! chan sliding-buffer close!]]
            [clojure.string :as str]
            [frontend.async :refer [raise!]]
            [frontend.components.about :as about]
            [frontend.components.common :as common]
            [frontend.components.shared :as shared]
            [frontend.datetime :as datetime]
            [frontend.state :as state]
            [frontend.stefon :as stefon]
            [frontend.utils :as utils :include-macros true]
            [om.core :as om :include-macros true])
  (:require-macros [frontend.utils :refer [html]]))

(defn build-state [app owner]
  (reify
    om/IDisplayName (display-name [_] "Admin Build State")
    om/IRender
    (render [_]
      (let [build-state (get-in app state/build-state-path)]
        (html
         [:section {:style {:padding-left "10px"}}
          [:a {:href "/api/v1/admin/build-state" :target "_blank"} "View raw"]
          " / "
          [:a {:on-click #(raise! owner [:refresh-admin-build-state-clicked])} "Refresh"]
          (if-not build-state
            [:div.loading-spinner common/spinner]
            [:code (om/build ankha/inspector build-state)])])))))

(defn fleet-state [app owner]
  (reify
    om/IDisplayName (display-name [_] "Admin Build State")
    om/IRender
    (render [_]
      (let [fleet-state (get-in app state/fleet-state-path)]
        (html
         [:section {:style {:padding-left "10px"}}
          [:header
           [:a {:href "/api/v1/admin/build-state-summary" :target "_blank"} "View raw"]
           " / "
           [:a {:on-click #(raise! owner [:refresh-admin-fleet-state-clicked])} "Refresh"]]
          (if-not fleet-state
            [:div.loading-spinner common/spinner]
            [:table
             [:thead
              [:tr
               [:th "Instance ID"]
               [:th "Instance Type"]
               [:th "Boot Time"]
               [:th "Busy Containers"]
               [:th "State"]]]
             [:tbody
              (for [instance fleet-state]
                [:tr
                 [:td (:instance_id instance)]
                 [:td (:ec2_instance_type instance)]
                 [:td (:boot_time instance)]
                 [:td (:busy instance) " / " (:total instance)]
                 [:td (:state instance)]])]])])))))

(defn admin [app owner]
  (reify
    om/IRender
    (render [_]
      (html
       [:div.container-fluid
        [:div.row-fluid
         [:div.span9
          [:p "Switch user"]
          [:form.form-inline {:method "post", :action "/admin/switch-user"}
           [:input.input-medium {:name "login", :type "text"}]
           [:input {:value (utils/csrf-token)
                    :name "CSRFToken",
                    :type "hidden"}]
           [:button.btn.btn-primary {:value "Switch user", :type "submit"}
            "Switch user"]]]]]))))
