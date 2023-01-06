# Disclaimer
Please note that this framework is in an early development phase and has not yet undergone thorough testing or optimization. As such, it may contain bugs or issues that could potentially cause problems or unexpected behaviour. This framework might be deprecated or archived at any point, due to changes in the game or the lack of personal time. Use at your own risk.

Please be aware that the API of this framework is subject to change as development progresses. It is not recommended to rely on specific implementation details or behaviour that may change in future updates. It is recommended to check for updates regularly and to adapt your code accordingly.

Since I'm still very unfamiliar with the game's API, I might create functionality that is inefficient or nonsensical in the scope of the current game API, or there may be easier and more straightforward ways to implement certain functionality. If you think that's the case, please have a chat with me!

Finally, the framework is not designed to support multiple instances of itself running concurrently. While it may be possible to do so, it is not supported nor (currently) enforced, and may result in unpredictable behaviour. Please ensure that only one instance of this framework is loaded at a time.

# Description
This framework is intended to serve as an abstraction layer for common routines and structures found in the game, functioning as an aggregator of multiple instances of different interface types, including IScriptEntity, ITickCapable, IRenderCapable, and ISerializable. These interfaces represent different capabilities that script entities can implement in order to participate in the framework.

One of the primary goals of this framework is to provide a more intuitive and easier-to-use interface for accessing the scripting capabilities of the base game. The base game's scripting capabilities can be difficult to understand and use, especially for developers who are unfamiliar with the underlying implementation. By providing a clear and consistent interface, this framework aims to make it easier for developers to create scripts and interact with the game.

Another goal of this framework is to intercept erroneous calls of the base game's API, by virtue of requiring developers to access the game's API through the framework. By routing all access to the base game's API through the framework, it becomes possible to validate and sanitize incoming calls, ensuring that developers do not put the game into an undefined state or cause other unintended consequences. This can help to improve the stability and reliability of scripts that use the framework, as well as protect against malicious or poorly-written scripts that could potentially cause problems.

# Usage
Porcupine partially wraps and replaces the script loading process of the game, providing four implementable interfaces (IRenderCapable, ISerializable, ITickCapable, and IScriptEntity) that provide the callbacks a script might need. Every Porcupine script (referred to as a module) must implement at least IScriptEntity. When Porcupine is initialized by the game, it looks for any attached .jars to the mods and tries to extract and initialize a module if one is found. From that point on, Porcupine forwards update and render calls from the game to all modules, along with save and load calls. A bootstrap project is available to help developers quickly get started creating scripts.

For the time being, a module is virtually identical to a vanilla game script, with the sole difference being that Porcupine handles update and render calls, as well as save and load calls.
