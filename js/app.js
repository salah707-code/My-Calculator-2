// app logic: audio (WebAudio short click), settings persistence, button wiring
(() => {
  // ---------- Audio: short click tone using WebAudio (no external file required) ----------
  const AudioEngine = (() => {
    let ctx = null;
    let enabled = true;
    function init() {
      if (ctx) return;
      try {
        ctx = new (window.AudioContext || window.webkitAudioContext)();
      } catch (e) {
        console.warn('WebAudio not supported', e);
        ctx = null;
      }
    }
    function playClick() {
      if (!enabled) return;
      if (!ctx) init();
      if (!ctx) return;
      try {
        const osc = ctx.createOscillator();
        const gain = ctx.createGain();
        osc.type = 'square';
        osc.frequency.value = 900; // Hz
        gain.gain.value = 0.0015; // very short soft click
        osc.connect(gain);
        gain.connect(ctx.destination);
        const now = ctx.currentTime;
        osc.start(now);
        gain.gain.setValueAtTime(0.0015, now);
        gain.gain.exponentialRampToValueAtTime(0.00001, now + 0.05);
        osc.stop(now + 0.06);
      } catch (err) {
        console.warn('Audio play error:', err);
      }
    }
    function setEnabled(v){ enabled = !!v; if (enabled) init(); }
    function isEnabled(){ return enabled; }
    return { playClick, setEnabled, isEnabled };
  })();

  // ---------- Settings handling ----------
  const storage = window.localStorage;
  function saveSetting(key, val){ storage.setItem(key, String(val)); }
  function loadSetting(key, fallback){ const v = storage.getItem(key); return v === null ? fallback : v; }

  // DOM refs
  const body = document.body;
  const btnShape = document.getElementById('btn-shape');
  const bigToggle = document.getElementById('big-toggle');
  const audioToggle = document.getElementById('audio-toggle');
  const resetBtn = document.getElementById('reset-settings');
  const display = document.getElementById('display');
  const numPad = document.getElementById('num-pad');

  // Initialize UI from storage
  const shapeInit = loadSetting('btnShape', 'rounded');
  body.classList.add('shape-' + shapeInit);
  btnShape.value = shapeInit;

  const bigInit = loadSetting('bigButtons', '0') === '1';
  bigToggle.checked = bigInit;
  body.classList.toggle('big-buttons', bigInit);

  const audioInit = loadSetting('audioEnabled', '1') === '1';
  audioToggle.checked = audioInit;
  AudioEngine.setEnabled(audioInit);

  // Event listeners for settings
  btnShape.addEventListener('change', (e) => {
    const v = e.target.value;
    body.classList.remove('shape-rounded','shape-square','shape-circular');
    body.classList.add('shape-' + v);
    saveSetting('btnShape', v);
  });

  bigToggle.addEventListener('change', (e) => {
    const v = !!e.target.checked;
    body.classList.toggle('big-buttons', v);
    saveSetting('bigButtons', v ? '1' : '0');
  });

  audioToggle.addEventListener('change', (e) => {
    const v = !!e.target.checked;
    AudioEngine.setEnabled(v);
    saveSetting('audioEnabled', v ? '1' : '0');
  });

  resetBtn.addEventListener('click', () => {
    saveSetting('btnShape', 'rounded');
    saveSetting('bigButtons', '0');
    saveSetting('audioEnabled', '1');
    location.reload();
  });

  // ---------- Calculator button handling (very basic demo) ----------
  let current = '';
  function updateDisplay(v){ display.textContent = v || '0'; }

  document.addEventListener('click', (e) => {
    const btn = e.target.closest('.calc-button');
    if (!btn) return;
    // Play click sound as immediate reaction
    try { AudioEngine.playClick(); } catch (err) { console.warn(err); }

    const txt = btn.textContent.trim();
    if (btn.id === 'equals') {
      try {
        // evaluate safely: simple replace of ×÷ if used
        const safe = current.replace(/[^0-9.+\-*/()%]/g, '');
        const res = safe ? eval(safe) : 0; // demo-only: replace with proper parser in prod
        current = String(res);
        updateDisplay(current);
      } catch (err) { updateDisplay('Error'); }
      return;
    }

    // support buttons that inserted double-zero via dataset
    if (btn.dataset && btn.dataset.value) {
      current += btn.dataset.value;
      updateDisplay(current);
      return;
    }

    // operators
    if (btn.classList.contains('op')) {
      current += ' ' + txt + ' ';
      updateDisplay(current);
      return;
    }

    // numbers and dot
    current += txt;
    updateDisplay(current);
  });

  // ensure a first user interaction unlocks WebAudio on mobile
  window.addEventListener('touchstart', function unlock() {
    try { if (AudioEngine && AudioEngine.isEnabled()) AudioEngine.playClick(); } catch (e){}
    window.removeEventListener('touchstart', unlock);
  });

})();
