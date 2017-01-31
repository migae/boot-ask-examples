(ns hello.speechlet.delegate
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

;; private SpeechletResponse getWelcomeResponse() {
(defn get-welcome-response
  []
  (let [speechText "Welcome to the Alexa Skills Kit, you can say hello"
        card (SimpleCard.)
        speech (PlainTextOutputSpeech.)
        reprompt (Reprompt.)

        ]
    (.setTitle card "HelloWorld")
    (.setContent card speechText)
    (.setText speech speechText)
    (.setOutputSpeech reprompt speech)
    (SpeechletResponse/newAskResponse speech, reprompt, card)))

;; /**
;; * Creates a {@code SpeechletResponse} for the hello intent.
;; *
;; * @return SpeechletResponse spoken and visual response for the given intent
;; */
;; private SpeechletResponse getHelloResponse() {
(defn get-hello-response
  []
  (let [speechText "Bonjour, world"
        card (SimpleCard.)
        speech (PlainTextOutputSpeech.)]
    (.setTitle card "HelloWorld")
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
  (let [speechText "You can say hello to me!"
        card (SimpleCard.)
        speech (PlainTextOutputSpeech.)
        reprompt (Reprompt.)]
    (.setTitle card "HelloWorld")
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
  (println "onLaunch")
  (get-welcome-response))

(defn speechlet-onIntent
  ^SpeechletResponse
  [this, ^IntentRequest request, ^Session session]
  (log/info (format "onIntent requestId=%s, sessionId=%s"
                    (.getRequestId request)
                    (.getSessionId session)))
  (let [intent (.getIntent request)
        intentName (if (nil? intent) nil (.getName intent))]
    (if (= "HelloWorldIntent" intentName)
      (get-hello-response)
      (if (= "AMAZON.HelpIntent" intentName)
        (get-help-response)
        (throw (SpeechletException. "Invalid Intent"))))))

(defn speechlet-onSessionEnded
  [this, ^SessionEndedRequest request ^Session session]
  (log/info (format "onSessionEnded requestId=%s, sessionId=%s"
                    (.getRequestId request)
                    (.getSessionId session)))
  )
