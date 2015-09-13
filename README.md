# Tenjin-2
An elegant room lighting and information system designed with UMass Amherst in mind

We started Tenjin with a simple goal in mind: to bring our boring dorm room into the 21st century. The first version of Tenjin supported only a simple informational screen with NPR news, current weather, and Reddit among others. During the summer of our second year, we embarked on Tenjin 2, a complete rebuild on Tenjin in NodeJS. Not only does Tenpin 2 provided its clasic information screen (now with support for 1080p monitors and proper scaling), but it also supports controlling LED strips for beautiful and elegant room lighting.

**Installation**
 1. Clone the Tenjin-2 repository on an internet facing machine (one with a static ip address, a rarity on most college campuses) and a local machine (one to act as the room controller) and some sort of build machine (Desktop/Laptop).
 2. On the remote machine, ensure that SSL certificates can be found in the locations specified by `ProxyRoute/proxy.js`. ProxyRoute is a server that can route GET requests over a SocketIO proxky, eliminating the need for port punching or static IP addresses at the college/school/room level.
 3. Rename the file `ProxyRoute/config_example.json` to config.json
 4. Inside the `config.json` file, add at least one user to the users dictionary, users have a plaintext name and a password hashed with sha512.
 5. Inside the same file, add at least one access key. Access keys are recommended to be random and long. A good way to generate an access key is to generate a random UUID and then to hash it with an algorithm like sha512.
 6. Start ProxyRoute by running `node proxy.js` or with a tool like PM2.
 7. On the local network machine, rename Tenpin Service/config_example.json to config.json
 8. Add the appropriate API keys (ForecastIO and NPR) as well as the ProxyRoute access key specified on the internet facing machine.
 9. On your development machine, compile and install both RoomLaunch (For rooted Kindle Fire's, it acts as a light switch) and TenjinMobile (A mobile room control platform for all android devices). TenjinMobile and RoomLaunch will need to have the appropriate local IP address specified in `TenjinRoom.java`. TenjinMobile will need to have `Config_example.java` renamed to `Config.java` and have its values updated accordingly.
 10. On the same machine, upload `tenjin_lights_firmware.io` to an Arduino board, the Arduino will act as the physical light controller. Light controllers will vary greatly from setup to setup, but the base will be the same. NodeJS sends opcodes to the Arduio, and the arduino in turn sends out PWM signals for controlling attached LED devices.
 11. Once the board is flashed, connect it to the room controller machine and start `Tenjijn\ Service/server.js` with the node command or PM2.
 12. If you configured everything correctly, at this point you should be able to connect via the mobile app and the kindle to control attached lighting devices. On the same computer, or another one connected to the same LAN, open http://<room controller IP<:3000 to view the room information screen.

**Included Software**

[Android Volley](https://android.googlesource.com/platform/frameworks/volley) - Apache2 License

[HoloColor Picker](https://github.com/LarsWerkman/HoloColorPicker) - Apache2 License
