/**
 * Setup.java
 * Created on 25 October 2003, 06:03
 */
package ren.gui.lookFeel;

import javax.swing.*;
import javax.swing.plaf.*;
import java.awt.color.*;
import java.awt.*;
import ren.gui.lookFeel.*;
/**
 *
 * @author  Rene Wooller
 */
public class Setup {
    
    public static void lookAndFeel() {
        UIDefaults uid = UIManager.getLookAndFeelDefaults();
        /*
        //sliders
      UIManager.put("Slider.background", 
            new ColorUIResource(CustomColors.get("back")));
      UIManager.put("Slider.focus", 
            new ColorUIResource(CustomColors.get("focus")));
      UIManager.put("Slider.foreground", 
            new ColorUIResource(CustomColors.get("fore")));
      UIManager.put("Slider.highlight",
         new ColorUIResource(CustomColors.get("hilight")));
      UIManager.put("Slider.shadow",
         new ColorUIResource(CustomColors.get("shad")));
      
      //panels
      UIManager.put("Panel.background",
         new ColorUIResource(CustomColors.get("back")));
      UIManager.put("Panel.foreground",
         new ColorUIResource(CustomColors.get("fore")));
      
      //buttons
      UIManager.put("Button.background",
         new ColorUIResource(CustomColors.get("back")));
      UIManager.put("Button.foreground",
         new ColorUIResource(Color.black));
      UIManager.put("Button.highlight",
         new ColorUIResource(CustomColors.get("hilight")));
      UIManager.put("Button.border",
         new ColorUIResource(CustomColors.get("back").darker().darker()));
      UIManager.put("Button.select",
         new ColorUIResource(CustomColors.get("hilight")));
      UIManager.put("Button.shadow",
         new ColorUIResource(CustomColors.get("shad")));
      UIManager.put("Button.darkShadow",
         new ColorUIResource(CustomColors.get("shad").darker()));
      UIManager.put("Button.font",
         LFonts.buttonLabel());
      //
      
      
      
      /*
       *Button.background = javax.swing.plaf.ColorUIResource[r=204,g=204,b=204]
Button.border = javax.swing.plaf.BorderUIResource$CompoundBorderUIResource@18f6235
Button.darkShadow = javax.swing.plaf.ColorUIResource[r=102,g=102,b=102]
Button.disabledText = javax.swing.plaf.ColorUIResource[r=153,g=153,b=153]
Button.focus = javax.swing.plaf.ColorUIResource[r=153,g=153,b=204]
Button.focusInputMap = javax.swing.plaf.InputMapUIResource@1c247a0
Button.font = javax.swing.plaf.FontUIResource[family=Dialog,name=Dialog,style=bold,size=12]
Button.foreground = javax.swing.plaf.ColorUIResource[r=0,g=0,b=0]
Button.highlight = javax.swing.plaf.ColorUIResource[r=255,g=255,b=255]
Button.light = javax.swing.plaf.ColorUIResource[r=255,g=255,b=255]
Button.margin = javax.swing.plaf.InsetsUIResource[top=2,left=14,bottom=2,right=14]
Button.select = javax.swing.plaf.ColorUIResource[r=153,g=153,b=153]
Button.shadow = javax.swing.plaf.ColorUIResource[r=153,g=153,b=153]
       **
      
      //toolbar
      UIManager.put("ToolBar.background",
      new ColorUIResource(CustomColors.get("back")));
      UIManager.put("ToolBar.floatingBackground",
      new ColorUIResource(CustomColors.get("back").getRed(), 
                          CustomColors.get("back").getGreen(), 
                          CustomColors.get("back").getBlue()));
      UIManager.put("ToolBar.floatingForeground",
      new ColorUIResource(CustomColors.get("fore").getRed(), 
                          CustomColors.get("fore").getGreen(), 
                          CustomColors.get("fore").getBlue()));
      
      /*
       *ToolBar.background = javax.swing.plaf.ColorUIResource[r=204,g=204,b=204]
ToolBar.border = javax.swing.plaf.metal.MetalBorders$ToolBarBorder@11c8a71
ToolBar.darkShadow = javax.swing.plaf.ColorUIResource[r=102,g=102,b=102]
ToolBar.dockingBackground = javax.swing.plaf.ColorUIResource[r=204,g=204,b=204]
ToolBar.dockingForeground = javax.swing.plaf.ColorUIResource[r=102,g=102,b=153]
ToolBar.floatingBackground = javax.swing.plaf.ColorUIResource[r=204,g=204,b=204]
ToolBar.floatingForeground = javax.swing.plaf.ColorUIResource[r=204,g=204,b=255]
ToolBar.font = javax.swing.plaf.FontUIResource[family=Dialog,name=Dialog,style=bold,size=12]
ToolBar.foreground = javax.swing.plaf.ColorUIResource[r=0,g=0,b=0]
ToolBar.highlight = javax.swing.plaf.ColorUIResource[r=255,g=255,b=255]
ToolBar.light = javax.swing.plaf.ColorUIResource[r=255,g=255,b=255]
ToolBar.separatorSize = javax.swing.plaf.DimensionUIResource[width=10,height=10]
ToolBar.shadow = javax.swing.plaf.ColorUIResource[r=153,g=153,b=153]
       **
      
      
      //scrollbar
      UIManager.put("ScrollBar.background",
      new ColorUIResource(CustomColors.get("back")));
      UIManager.put("ScrollBar.foreGround",
      new ColorUIResource(CustomColors.get("fore")));
      UIManager.put("ScrollBar.darkShadow",
      new ColorUIResource(CustomColors.get("shad").darker()));
      UIManager.put("ScrollBar.thumb",
      new ColorUIResource(CustomColors.get("control")));
      UIManager.put("ScrollBar.thumbDarkShadow",
      new ColorUIResource(CustomColors.get("control").darker().darker()));
      UIManager.put("ScrollBar.thumbShadow",
      new ColorUIResource(CustomColors.get("control").darker()));
      
      UIManager.put("ScrollBar.thumbHighlight",
      new ColorUIResource(CustomColors.get("controlHighlight")));
      UIManager.put("ScrollBar.track",
      new ColorUIResource(CustomColors.get("back")));
      UIManager.put("ScrollBar.trackHighlight",
      new ColorUIResource(CustomColors.get("hilight")));
      UIManager.put("scrollBar",
      new ColorUIResource(CustomColors.get("norm")));
      
      /*
       *ScrollBar.allowsAbsolutePositioning = true
ScrollBar.background = javax.swing.plaf.ColorUIResource[r=204,g=204,b=204]
ScrollBar.darkShadow = javax.swing.plaf.ColorUIResource[r=102,g=102,b=102]
ScrollBar.focusInputMap = javax.swing.plaf.InputMapUIResource@1ec6696
ScrollBar.focusInputMap.RightToLeft = javax.swing.plaf.InputMapUIResource@1faba46
ScrollBar.foreground = javax.swing.plaf.ColorUIResource[r=204,g=204,b=204]
ScrollBar.highlight = javax.swing.plaf.ColorUIResource[r=255,g=255,b=255]
ScrollBar.maximumThumbSize = javax.swing.plaf.DimensionUIResource[width=4096,height=4096]
ScrollBar.minimumThumbSize = javax.swing.plaf.DimensionUIResource[width=8,height=8]
ScrollBar.shadow = javax.swing.plaf.ColorUIResource[r=153,g=153,b=153]
ScrollBar.thumb = javax.swing.plaf.ColorUIResource[r=153,g=153,b=204]
ScrollBar.thumbDarkShadow = javax.swing.plaf.ColorUIResource[r=102,g=102,b=102]
ScrollBar.thumbHighlight = javax.swing.plaf.ColorUIResource[r=204,g=204,b=255]
ScrollBar.thumbShadow = javax.swing.plaf.ColorUIResource[r=102,g=102,b=153]
ScrollBar.track = javax.swing.plaf.ColorUIResource[r=204,g=204,b=204]
ScrollBar.trackHighlight = javax.swing.plaf.ColorUIResource[r=102,g=102,b=102]
ScrollBar.width = 17
ScrollBarUI = javax.swing.plaf.metal.MetalScrollBarUI
       *
      
      
      //scrollPane
      UIManager.put("ScrollPane.border",
      new ColorUIResource(CustomColors.get("shad")));
      /*
ScrollPane.ancestorInputMap = javax.swing.plaf.InputMapUIResource@1b26af3
ScrollPane.ancestorInputMap.RightToLeft = javax.swing.plaf.InputMapUIResource@100ab23
ScrollPane.background = javax.swing.plaf.ColorUIResource[r=204,g=204,b=204]
ScrollPane.border = javax.swing.plaf.metal.MetalBorders$ScrollPaneBorder@1968e23
ScrollPane.font = javax.swing.plaf.FontUIResource[family=Dialog,name=Dialog,style=plain,size=12]
ScrollPane.foreground = javax.swing.plaf.ColorUIResource[r=0,g=0,b=0]
       *
      
      // combobox
      UIManager.put("ComboBox.background",
      new ColorUIResource(CustomColors.get("back")));
      UIManager.put("ComboBox.buttonDarkShadow",
      new ColorUIResource(CustomColors.get("shad").darker()));
      UIManager.put("ComboBox.buttonShadow",
      new ColorUIResource(CustomColors.get("shad")));
      /*
      ComboBox.ancestorInputMap = javax.swing.plaf.InputMapUIResource@503429
ComboBox.background = javax.swing.plaf.ColorUIResource[r=204,g=204,b=204]
ComboBox.buttonBackground = javax.swing.plaf.ColorUIResource[r=204,g=204,b=204]
ComboBox.buttonDarkShadow = javax.swing.plaf.ColorUIResource[r=102,g=102,b=102]
ComboBox.buttonHighlight = javax.swing.plaf.ColorUIResource[r=255,g=255,b=255]
ComboBox.buttonShadow = javax.swing.plaf.ColorUIResource[r=153,g=153,b=153]
ComboBox.disabledBackground = javax.swing.plaf.ColorUIResource[r=204,g=204,b=204]
ComboBox.disabledForeground = javax.swing.plaf.ColorUIResource[r=153,g=153,b=153]
ComboBox.font = javax.swing.plaf.FontUIResource[family=Dialog,name=Dialog,style=bold,size=12]
ComboBox.foreground = javax.swing.plaf.ColorUIResource[r=0,g=0,b=0]
ComboBox.selectionBackground = javax.swing.plaf.ColorUIResource[r=153,g=153,b=204]
ComboBox.selectionForeground = javax.swing.plaf.ColorUIResource[r=0,g=0,b=0]
      *
      
      
      //textField
      
      UIManager.put("TextField.background",
      new ColorUIResource(CustomColors.get("hilight")));
      UIManager.put("TextField.border",
      new ColorUIResource(CustomColors.get("shad")));

      UIManager.put("TextField.selectionBackground",
      new ColorUIResource(CustomColors.get("selected")));
  //    UIManager.put("ScrollBar.background",
  //    new ColorUIResource(CustomColors.get("back));
      /*TextField.background = javax.swing.plaf.ColorUIResource[r=255,g=255,b=255]
TextField.border = javax.swing.plaf.BorderUIResource$CompoundBorderUIResource@1415de6
TextField.caretBlinkRate = 500
TextField.caretForeground = javax.swing.plaf.ColorUIResource[r=0,g=0,b=0]
TextField.darkShadow = javax.swing.plaf.ColorUIResource[r=102,g=102,b=102]
TextField.focusInputMap = javax.swing.plaf.InputMapUIResource@1e152c5
TextField.font = javax.swing.plaf.FontUIResource[family=Dialog,name=Dialog,style=plain,size=12]
TextField.foreground = javax.swing.plaf.ColorUIResource[r=0,g=0,b=0]
TextField.highlight = javax.swing.plaf.ColorUIResource[r=255,g=255,b=255]
TextField.inactiveBackground = javax.swing.plaf.ColorUIResource[r=204,g=204,b=204]
TextField.inactiveForeground = javax.swing.plaf.ColorUIResource[r=153,g=153,b=153]
TextField.light = javax.swing.plaf.ColorUIResource[r=255,g=255,b=255]
TextField.margin = javax.swing.plaf.InsetsUIResource[top=0,left=0,bottom=0,right=0]
TextField.selectionBackground = javax.swing.plaf.ColorUIResource[r=204,g=204,b=255]
TextField.selectionForeground = javax.swing.plaf.ColorUIResource[r=0,g=0,b=0]
TextField.shadow = javax.swing.plaf.ColorUIResource[r=153,g=153,b=153]
       *
      
      
      //tab pane
      UIManager.put("TabbedPane.background",
      new ColorUIResource(CustomColors.get("back")));
      
      UIManager.put("TabbedPane.darkShadow",
      new ColorUIResource(CustomColors.get("shad").darker()));
      
      UIManager.put("TabbedPane.foreground",
      new ColorUIResource(Color.black));//CustomColors.get("fore")));
      
      
      UIManager.put("TabbedPane.selected",
      new ColorUIResource(CustomColors.get("hilight")));
      UIManager.put("TabbedPane.border",
      new ColorUIResource(CustomColors.get("shad")));
      UIManager.put("TabbedPane.shadow",
      new ColorUIResource(CustomColors.get("shad")));
      
      UIManager.put("TabbedPane.tabAreaBackground",
      new ColorUIResource(Math.max(0, CustomColors.get("hilight").getRed()-30), 
                          CustomColors.get("hilight").getGreen(), 
                          CustomColors.get("hilight").getBlue()));
      
      UIManager.put("TabbedPane.light",
      new ColorUIResource(CustomColors.get("hilight")));
      UIManager.put("TabbedPane.selectHighlight",
      new ColorUIResource(CustomColors.get("hilight").getRed(), 
                          Math.min(CustomColors.get("hilight").getGreen(), 
                                   CustomColors.get("hilight").getGreen()+20), 
                          CustomColors.get("hilight").getBlue()));
      
      UIManager.put("TabbedPane.highlight",
      new ColorUIResource(CustomColors.get("hilight")));
      /*
       *TabbedPane.background = javax.swing.plaf.ColorUIResource[r=153,g=153,b=153]
TabbedPane.contentBorderInsets = javax.swing.plaf.InsetsUIResource[top=2,left=2,bottom=3,right=3]
TabbedPane.darkShadow = javax.swing.plaf.ColorUIResource[r=102,g=102,b=102]
TabbedPane.focus = javax.swing.plaf.ColorUIResource[r=102,g=102,b=153]
TabbedPane.focusInputMap = javax.swing.plaf.InputMapUIResource@341960
TabbedPane.font = javax.swing.plaf.FontUIResource[family=Dialog,name=Dialog,style=bold,size=12]
TabbedPane.foreground = javax.swing.plaf.ColorUIResource[r=0,g=0,b=0]
TabbedPane.highlight = javax.swing.plaf.ColorUIResource[r=255,g=255,b=255]
TabbedPane.light = javax.swing.plaf.ColorUIResource[r=204,g=204,b=204]
TabbedPane.selectHighlight = javax.swing.plaf.ColorUIResource[r=255,g=255,b=255]
TabbedPane.selected = javax.swing.plaf.ColorUIResource[r=204,g=204,b=204]
TabbedPane.selectedTabPadInsets = javax.swing.plaf.InsetsUIResource[top=2,left=2,bottom=2,right=1]
TabbedPane.shadow = javax.swing.plaf.ColorUIResource[r=153,g=153,b=153]
TabbedPane.tabAreaBackground = javax.swing.plaf.ColorUIResource[r=204,g=204,b=204]
TabbedPane.tabAreaInsets = javax.swing.plaf.InsetsUIResource[top=4,left=2,bottom=0,right=6]
TabbedPane.tabInsets = javax.swing.plaf.InsetsUIResource[top=0,left=9,bottom=1,right=9]
TabbedPane.tabRunOverlay = 2
TabbedPane.textIconGap = 4
       **
      
      UIManager.put("Menu.background",
      new ColorUIResource(CustomColors.get("back")));
      UIManager.put("Menu.selectionBackground",
      new ColorUIResource(CustomColors.get("selected")));
      
      UIManager.put("MenuBar.background",
      new ColorUIResource(CustomColors.get("back")));
      UIManager.put("MenuBar.foreground",
      new ColorUIResource(CustomColors.get("fore")));
      
      UIManager.put("MenuBar.border",
      new ColorUIResource(CustomColors.get("shad")));

      UIManager.put("MenuBar.highlight",
      new ColorUIResource(CustomColors.get("highlight")));
      
      
      UIManager.put("MenuItem.background",
      new ColorUIResource(CustomColors.get("hilight")));

      UIManager.put("MenuItem.selectionBackground",
      new ColorUIResource(CustomColors.get("selected")));
      
        UIManager.put("PopupMenu.border",
        new ColorUIResource(CustomColors.get("shad")));
      
       UIManager.put("PopupMenu.background",
        new ColorUIResource(CustomColors.get("hilight")));
       
        UIManager.put("PopupMenu.foreGround",
        new ColorUIResource(CustomColors.get("fore")));
      
        UIManager.put("activeCaption",
        new ColorUIResource(CustomColors.get("selected")));
      
        
    //  UIManager.put("MenuItem.foreground",
    //  new ColorUIResource(CustomColors.get("hili"));
      
      
//      UIManager.put("Slider.horizontalThumbIcon",
//         greenLF.GreenIconFactory.getHorizontalSliderThumbIcon());
        
        /*
        Slider.background = javax.swing.plaf.ColorUIResource[r=204,g=204,b=204]
        Slider.focus = javax.swing.plaf.ColorUIResource[r=153,g=153,b=204]
     //   Slider.focusInputMap = javax.swing.plaf.InputMapUIResource@e3b895
     //   Slider.focusInputMap.RightToLeft = javax.swing.plaf.InputMapUIResource@18a7efd
      //  Slider.focusInsets = javax.swing.plaf.InsetsUIResource[top=0,left=0,bottom=0,right=0]
        Slider.foreground = javax.swing.plaf.ColorUIResource[r=153,g=153,b=204]
        Slider.highlight = javax.swing.plaf.ColorUIResource[r=255,g=255,b=255]
        Slider.horizontalThumbIcon = javax.swing.plaf.metal.MetalIconFactory$HorizontalSliderThumbIcon@15cda3f
    */
    }
    
}
