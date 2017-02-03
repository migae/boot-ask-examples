(ns session.speechlet.delegate
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
           [java.io InputStream ByteArrayInputStream]
           [java.util Collections]
           [java.lang IllegalArgumentException RuntimeException])
  (:require [clojure.string :as str]
            [clojure.tools.logging :as log :refer [debug info]] ;; :trace, :warn, :error, :fatal
            [session.speechlet.intents :as intent]))

(def COLOR-KEY  "COLOR")
(def COLOR-SLOT "Color")

(def speech
  {:cancel  "Goodbye, cancelling"
   :help    (str "You can tell me your favorite color, "
                 "and I will remember it for the duration of this session.")
   :help-ask       (str "You can ask me your favorite color by saying, what's my favorite color")
   :favorite-color (str "I now know that your favorite color is %s. "
                        "You can ask me your favorite color by saying, what's my favorite color?")
   :favorite-stmt "Your favorite color is %s. Goodbye."
   :try-again "I'm not sure what your favorite color is, please try again."
   :not-sure  (str "I'm not sure what your favorite color is. "
                       "You can tell me your favorite color by saying, my favorite color is red")

   :stop    "Goodbye, I'm stopping"
   :welcome (str "Welcome to the Alexa Skills Kit session sample. "
                 "Please tell me your favorite color by saying, my favorite color is red")
   :welcome-reprompt "Please tell me your favorite color by saying, my favorite color is red"})

(def card
  {:session "Session"})

(defn get-speechlet-response
  [speech-text reprompt-text is-ask-response?]
  (let [
        card (SimpleCard.)
        speech (PlainTextOutputSpeech.)]
    (.setTitle card (:session card))
    (.setContent card speech-text)
    (.setText speech speech-text)
    (if is-ask-response?
      (let [reprompt (Reprompt.)]
        (.setOutputSpeech reprompt speech)
        (SpeechletResponse/newAskResponse speech, reprompt, card))
      (SpeechletResponse/newTellResponse speech, card))))

(defn get-welcome-response
  []
  (get-speechlet-response (:welcome speech) (:welcome-reprompt speech)))

(defn set-color-in-session
  [intent, session]
  (let [slots (.getSlots intent)]
    (if-let [favorite-color (.getValue (.get slots COLOR-SLOT))]
      (let [speech-text (format (:favorite-color speech) favorite-color)
            reprompt-text (:help-ask speech)]
        (.setAttribute session COLOR-KEY favorite-color)
        (get-speechlet-response speech-text reprompt-text true))
      (let [speech-text (:try-again speech)
            reprompt-text (:not-sure speech)]
        (get-speechlet-response speech-text reprompt-text true)))))

(defn get-color-from-session
  [intent, session]
  (let [favorite-color (.getAttribute session COLOR-KEY)
        speech-text (if (str/blank? favorite-color)
                      (:not-sure speech)
                      (format (:favorite-stmt speech) favorite-color))]
    (get-speechlet-response speech-text speech-text (not (str/blank? favorite-color)))))

(defn get-help-response []
  (let [speech-text (:help speech)
        card (SimpleCard.)
        speech (PlainTextOutputSpeech.)
        reprompt (Reprompt.)]
    (.setTitle card (:goodbye card))
    (.setContent card speech-text)
    (.setText speech speech-text)
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
  (log/info (format "onIntent requestId=%s, sessionId=%s, intent=%s"
                    (.getRequestId request)
                    (.getSessionId session)
                    (if-let [i (.getIntent request)] (.getName i) nil)))
  (let [intent (.getIntent request)
        intent-name (if (nil? intent) nil (.getName intent))]
    (cond
      (= intent-name intent/my-color!) (set-color-in-session intent session)
      (= intent-name intent/my-color?) (get-color-from-session intent session)
      (= intent-name intent/help) (get-help-response)
      :else (throw (SpeechletException. "Invalid Intent")))))

(defn speechlet-onSessionEnded
  [this, ^SessionEndedRequest request ^Session session]
  (log/info (format "onSessionEnded requestId=%s, sessionId=%s"
                    (.getRequestId request)
                    (.getSessionId session)))
  )
