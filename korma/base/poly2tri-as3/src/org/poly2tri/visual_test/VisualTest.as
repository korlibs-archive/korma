package org.poly2tri.visual_test {
	import flash.display.MovieClip;
	import flash.display.StageQuality;
	import flash.events.Event;
	import flash.events.MouseEvent;
	import flash.filters.DropShadowFilter;
	import flash.text.TextField;
	import flash.text.TextFieldAutoSize;
	import flash.text.TextFormat;
	import flash.utils.setInterval;
	import org.poly2tri.Point;

    public class VisualTest extends MovieClip {
		protected var visiblePolygonTest:VisiblePolygonTest;
		protected var startPoint:Point;
		protected var endPoint:Point;
		

        public function VisualTest() {
			visiblePolygonTest = new VisiblePolygonTest();
			addChild(visiblePolygonTest);
			//visiblePolygonTest.x = stage.stageWidth / 2;
			//visiblePolygonTest.y = stage.stageHeight / 2;
			visiblePolygonTest.scaleY = visiblePolygonTest.scaleX = 1.0;
			visiblePolygonTest.cacheAsBitmap = true;
			
			startPoint = new Point(50, 50);
			endPoint = new Point(300, 300);
			
			addEventListener(Event.ENTER_FRAME, onEnterFrame);
			stage.addEventListener(MouseEvent.MOUSE_MOVE , onMouseMove);
			
			var tf:TextField = new TextField();
			tf.text = "Press mouse button to set End point or Start point pressing ctrl.";
			tf.setTextFormat(new TextFormat("Arial", 12));
			tf.x = 6;
			tf.y = 6;
			tf.autoSize = TextFieldAutoSize.LEFT;
			tf.selectable = false;
			tf.filters = [new DropShadowFilter(0, 0, 0xFFFFFF, 1, 4, 4, 3, 1)];
			addChild(tf);
			
			doPathFind();
        }
		
		private function doPathFind():void {
			this.visiblePolygonTest.doPathFind(startPoint, endPoint);
		}
		
		private function processMouseEvent(e:MouseEvent):void {

		}
		
		private function onMouseMove(e:MouseEvent):void {
			if (e.buttonDown) {
				if (e.ctrlKey) {
					startPoint = new Point(e.stageX, e.stageY);
				} else {
					endPoint = new Point(e.stageX, e.stageY);
				}
				doPathFind();
			}
		}

		
		protected function onEnterFrame(e:Event):void {
			//visiblePolygonTest.rotationZ += (1 / this.stage.frameRate) * 100;
		}
    }
}
