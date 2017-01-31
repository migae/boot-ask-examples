(ns goodbye.speechlet.delegate
  "gen-class :implements com.amazon.speech.speechlet.Speechlet"
  ;;(:refer-clojure :exclude [read read-string])
  (:import [com.amazon.speech.slu Intent]
           [com.amazon.speech.speechlet
            IntentRequest
            LaunchRequest
            Session
            SessionEndedRequest
            SessionStartedRequest
            Speechlet
            SpeechletException
            SpeechletResponse]
           [com.amazon.speech.ui
            PlainTextOutputSpeech
            Reprompt
            SimpleCard]

           #_[com.google.appengine.api.datastore EntityNotFoundException]
           [java.io InputStream ByteArrayInputStream]
           [java.util Collections]
           [java.lang IllegalArgumentException RuntimeException])
  (:require [clojure.tools.logging :as log :refer [debug info]] ;; :trace, :warn, :error, :fatal
            ))

(def intents
  {:goodbye {:intent "GoodbyeIntent" :response "Hasta la vista, baby"}
   :help    {:intent "AMAZON.HelpIntent"}})

;; private SpeechletResponse getWelcomeResponse() {
(defn get-welcome-response
  []
  (let [speechText "Welcome to the Alexa Skills Kit, you can say goodbye"
        card (SimpleCard.)
        speech (PlainTextOutputSpeech.)
        reprompt (Reprompt.)

        ]
    (.setTitle card "GoodbyeWorld")
    (.setContent card speechText)
    (.setText speech speechText)
    (.setOutputSpeech reprompt speech)
    (SpeechletResponse/newAskResponse speech, reprompt, card)))

;; /**
;; * Creates a {@code SpeechletResponse} for the goodbye intent.
;; *
;; * @return SpeechletResponse spoken and visual response for the given intent
;; */
;; private SpeechletResponse getGoodbyeResponse() {
(defn get-goodbye-response
  []
  (let [speechText (-> intents :goodbye :response)
        card (SimpleCard.)
        speech (PlainTextOutputSpeech.)]
    (.setTitle card "GoodbyeWorld")
    (.setContent card speechText)
    (.setText speech speechText)
    (SpeechletResponse/newTellResponse speech card)))

;; /**
;; * Creates a {@code SpeechletResponse} for the help intent.
;; *
;; * @return SpeechletResponse spoken and visual response for the given intent
;; */
;; private SpeechletResponse getHelpResponse() {
(defn get-help-response []
  (let [speechText "You can say goodbye to me!"
        card (SimpleCard.)
        speech (PlainTextOutputSpeech.)
        reprompt (Reprompt.)]
    (.setTitle card "GoodbyeWorld")
    (.setContent card speechText)
    (.setText speech speechText)
    (.setOutputSpeech reprompt speech)
    (SpeechletResponse/newAskResponse speech, reprompt, card)))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; com.amazon.speech.speechlet.Speechlet API

(defn speechlet-onSessionStarted
  [this, request, session]
  (log/info (format "onSessionStarted requestId=%s, sessionId=%s"
                    (.getRequestId request)
                    (.getSessionId session)))
  ;; any initialization logic goes here
  )

(defn speechlet-onLaunch
  ^SpeechletResponse
  [this, ^LaunchRequest request, ^Session session]
  (log/info (format "onLaunch requestId=%s, sessionId=%s"
                    (.getRequestId request)
                    (.getSessionId session)))
  (get-welcome-response))

(defn speechlet-onIntent
  ^SpeechletResponse
  [this, ^IntentRequest request, ^Session session]
  (log/info (format "onIntent requestId=%s, sessionId=%s"
                    (.getRequestId request)
                    (.getSessionId session)))
  (let [intent (.getIntent request)
        intentName (if (nil? intent) nil (.getName intent))]
    (if (= (-> intents :goodbye :intent) intentName)
      (get-goodbye-response)
      (if (= (-> intents :help :intent) intentName)
        (get-help-response)
        (throw (SpeechletException. "Invalid Intent"))))))

(defn speechlet-onSessionEnded
  [this, ^SessionEndedRequest request ^Session session]
  (log/info (format "onSessionEnded requestId=%s, sessionId=%s"
                    (.getRequestId request)
                    (.getSessionId session)))
  )
