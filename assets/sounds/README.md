# Click sound placeholder

This directory is intended to hold the optional click sound file used by the calculator UI.

Please add a small click sound file named `click.mp3` in this folder if you prefer a file-based click sound instead of the built-in WebAudio tone.

Path to add the file: `assets/sounds/click.mp3`

If you add the file, you can modify `js/app.js` to load and play the file instead of using the WebAudio generator. Example snippet:

```js
// Replace AudioEngine.playClick implementation with file-based audio
const clickAudio = new Audio('/assets/sounds/click.mp3');
clickAudio.preload = 'auto';
function playClick(){ try { clickAudio.currentTime = 0; clickAudio.play(); } catch(e){ console.warn(e); } }
```
