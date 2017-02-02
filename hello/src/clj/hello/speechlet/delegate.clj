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
            [-ask.intent :as intent]
            ))

(def speech
  {:hello "Why, howdy, there, from GAE"
   ;;  "Ohayo, gozai masoo, from a Lambda function!"
   :welcome "Welcome to the Alexa Skills Kit, you can say hello"
   :help "You can say hello to me!"})

(def card
  {:hello "HelloWorld"})

(defn get-welcome-response
  []
  (let [speechText (:welcome speech)
        a-card (SimpleCard.)
        a-speech (PlainTextOutputSpeech.)
        reprompt (Reprompt.)]
    (.setTitle a-card (:hello card))
    (.setContent a-card speechText)
    (.setText a-speech speechText)
    (.setOutputSpeech reprompt a-speech)
    (SpeechletResponse/newAskResponse a-speech, reprompt, a-card)))

(defn get-hello-response
  "Creates a {@code SpeechletResponse} for the hello intent.

  @return SpeechletResponse spoken and visual response for the given intent"
  []
  (let [speechText (:hello speech)
        a-card (SimpleCard.)
        speech (PlainTextOutputSpeech.)]
    (.setTitle a-card (:hello card))
    (.setContent a-card speechText)
    (.setText speech speechText)
    (SpeechletResponse/newTellResponse speech a-card)))

(defn get-help-response
  "Creates a {@code SpeechletResponse} for the help intent.

  @return SpeechletResponse spoken and visual response for the given intent"
  []
  (let [speechText (:help speech)
        a-card (SimpleCard.)
        speech (PlainTextOutputSpeech.)
        reprompt (Reprompt.)]
    (.setTitle a-card (:hello card))
    (.setContent a-card speechText)
    (.setText speech speechText)
    (.setOutputSpeech reprompt speech)
    (SpeechletResponse/newAskResponse speech, reprompt, a-card)))

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
    (if (= intent/hello intentName)
      (get-hello-response)
      (if (= intent/help intentName)
        (get-help-response)
        (throw (SpeechletException. "Invalid Intent"))))))

(defn speechlet-onSessionEnded
  [this, ^SessionEndedRequest request ^Session session]
  (log/info (format "onSessionEnded requestId=%s, sessionId=%s"
                    (.getRequestId request)
                    (.getSessionId session)))
  )
