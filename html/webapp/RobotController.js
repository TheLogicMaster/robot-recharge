let delay = m => new Promise(r => setTimeout(r, m));

async function delayCheck(duration) {
   let time = duration;
   let isPaused = false;
   while (time > 0) {
       await delay($wnd.Robot.isFast() ? 10 : 20);
       if ($wnd.Robot.isStopped())
           return true;
       if (!$wnd.Robot.isPaused()) {
           if (!$wnd.Robot.isWaiting())
               time -= 20;
       } else if (!isPaused)
           $wnd.Robot.onPause();
       isPaused = $wnd.Robot.isPaused();
   }
}

async function move(distance) {
   $wnd.Robot.incrementCalls();
   $wnd.Robot.loopAnimation('Armature|MoveForward');
   for (let i = 0; i < Math.abs(distance); i++) {
       if (await delayCheck(1))
           return true;
       if ($wnd.Robot.checkFloor(distance)) {
           $wnd.Robot.playAnimationSpeed('Armature|Ledge', 0.3);
           await delayCheck(1500);
           return;
       }
       if ($wnd.Robot.checkCrash(distance)) {
           $wnd.Robot.playAnimationSpeed('Armature|Crash', 1);
           await delayCheck(100);
           $wnd.Robot.onCrash(distance);
           await delayCheck(500);
           return;
       }
       let time = 1 / $wnd.Robot.speed;
       while (time > 0) {
           $wnd.Robot.subMove(distance);
           if (await delayCheck(20))
               return true;
           time -= 0.02;
       }
       $wnd.Robot.move(distance);
   }
   $wnd.Robot.stopAnimation();
}

async function turn(distance) {
   $wnd.Robot.incrementCalls();
   for (let i = 0; i < Math.abs(distance); i++) {
       let time = 90 / $wnd.Robot.rotationSpeed;
       while (time > 0) {
           $wnd.Robot.subTurn(distance);
           if (await delayCheck(20))
               return true;
           time -= 0.02;
       }
       $wnd.Robot.turn(distance);
   }
}

async function sleep(duration) {
   $wnd.Robot.incrementCalls();
   await delayCheck(duration * 1000);
}