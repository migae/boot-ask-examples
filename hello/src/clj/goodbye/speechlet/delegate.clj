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
            [-ask.intent :as intent]))

(def speech
  {:goodbye "GOODBYE, baby"
   :help    "You can say goodbye to me!"
   :welcome "Welcome to the Alexa Skills Kit, you can say goodbye"})

(def card
  {:goodbye "GoodbyeWorld"})

(defn get-welcome-response
  []
  (let [speechText (:welcome speech)
        card (SimpleCard.)
        speech (PlainTextOutputSpeech.)
        reprompt (Reprompt.)]
    (.setTitle card (:goodbye card))
    (.setContent card speechText)
    (.setText speech speechText)
    (.setOutputSpeech reprompt speech)
    (SpeechletResponse/newAskResponse speech, reprompt, card)))

(defn get-goodbye-response
  []
  (let [speechText (:goodbye speech)
        card (SimpleCard.)
        speech (PlainTextOutputSpeech.)]
    (.setTitle card (:goodbye card))
    (.setContent card speechText)
    (.setText speech speechText)
    (SpeechletResponse/newTellResponse speech card)))

(defn get-help-response []
  (let [speechText (:help speech)
        card (SimpleCard.)
        speech (PlainTextOutputSpeech.)
        reprompt (Reprompt.)]
    (.setTitle card (:goodbye card))
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
    (if (= intent/goodbye intentName)
      (get-goodbye-response)
      (if (= intent/help intentName)
        (get-help-response)
        (throw (SpeechletException. "Invalid Intent"))))))

(defn speechlet-onSessionEnded
  [this, ^SessionEndedRequest request ^Session session]
  (log/info (format "onSessionEnded requestId=%s, sessionId=%s"
                    (.getRequestId request)
                    (.getSessionId session)))
  )
