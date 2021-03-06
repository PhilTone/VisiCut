/**
 * This file is part of VisiCut.
 * Copyright (C) 2011 - 2013 Thomas Oster <thomas.oster@rwth-aachen.de>
 * RWTH Aachen University - 52062 Aachen, Germany
 *
 *     VisiCut is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU Lesser General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     VisiCut is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU Lesser General Public License for more details.
 *
 *     You should have received a copy of the GNU Lesser General Public License
 *     along with VisiCut.  If not, see <http://www.gnu.org/licenses/>.
 **/
package de.thomas_oster.visicut.misc;

import de.thomas_oster.uicomponents.AngleTextfield;
import de.thomas_oster.uicomponents.ImageComboBox;
import de.thomas_oster.uicomponents.LengthTextfield;
import de.thomas_oster.uicomponents.UnitTextfield;
import java.awt.Component;
import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.swing.Box;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;

/**
 *
 * @author Thomas Oster <thomas.oster@rwth-aachen.de>
 */
public class DialogHelper
{

  private Component parent;
  private String title;

  public DialogHelper(Component parent, String title)
  {
    this.parent = parent;
    this.title = title;
  }

  public void openInFilebrowser(File f)
  {
    try
    {
      Desktop d = Desktop.getDesktop();
      if (d.isSupported(Desktop.Action.OPEN))
      {
        d.open(f.isDirectory() ? f : f.getParentFile());
        return;
      }
    }
    catch (Exception e)
    {
    }
    if (Helper.isMacOS())
    {
      try
      {
        Runtime.getRuntime().exec(new String[]{"open", (f.isDirectory() ? f : f.getParentFile()).getAbsolutePath()});
      }
      catch (Exception e)
      {
      }
    }
    showErrorMessage("Sorry, can not open files on your plaftorm");
  }

  public void openInEditor(File f)
  {
    try
    {
      Desktop d = Desktop.getDesktop();
      if (d.isSupported(Desktop.Action.EDIT))
      {
        d.edit(f);
        return;
      }
      else if (d.isSupported(Desktop.Action.OPEN))
      {
        d.open(f);
        return;
      }
    }
    catch (Exception e)
    {
    }
    if (Helper.isMacOS())
    {
      try
      {
        Runtime.getRuntime().exec(new String[]{"open", f.getAbsolutePath()});
        return;
      }
      catch (IOException ex)
      {
      }
    }
    showErrorMessage("Sorry, can not open files on your plaftorm");
  }

  public <T> T askElement(Collection<T> source, String text)
  {
    Box b = Box.createVerticalBox();
    ImageComboBox cb = new ImageComboBox();
    b.add(new JLabel(text));
    b.add(cb);
    for(T e:source)
    {
      cb.addItem(e);
    }
    if (JOptionPane.showConfirmDialog(parent, b, title, JOptionPane.OK_CANCEL_OPTION, JOptionPane.DEFAULT_OPTION) == JOptionPane.OK_OPTION)
    {
      return (T) cb.getSelectedItem();
    }
    return null;
  }

  public String askString(String initial, String text)
  {
    Box b = Box.createVerticalBox();
    JTextField tf = new JTextField();
    tf.setText(initial);
    b.add(new JLabel(text));
    b.add(tf);
    if (JOptionPane.showConfirmDialog(parent, b, title, JOptionPane.OK_CANCEL_OPTION, JOptionPane.DEFAULT_OPTION) == JOptionPane.OK_OPTION)
    {
      return tf.getText();
    }
    return null;
  }

  public boolean showYesNoQuestion(String text)
  {
    return JOptionPane.showConfirmDialog(parent, text, title, JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE) == JOptionPane.YES_OPTION;
  }

  public boolean showOkCancelQuestion(String text)
  {
    return JOptionPane.showConfirmDialog(parent, text, title, JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE) == JOptionPane.OK_OPTION;
  }

  public boolean showYesNoDialog(String text)
  {
    return JOptionPane.showConfirmDialog(parent, text, title, JOptionPane.YES_NO_OPTION, JOptionPane.PLAIN_MESSAGE) == JOptionPane.YES_OPTION;
  }

  public boolean showOkCancelDialog(String text)
  {
    return JOptionPane.showConfirmDialog(parent, text, title, JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE) == JOptionPane.OK_OPTION;
  }

  public void showWarningMessage(List<String> text)
  {
    if (text == null || text.isEmpty())
    {
      return;
    }
    String txt = "";
    for(String s : text)
    {
      txt += s + "\n";
    }
    this.showWarningMessage(txt);
  }

  public void showWarningMessage(String text)
  {
    JOptionPane.showMessageDialog(parent, text, title, JOptionPane.WARNING_MESSAGE);
  }
  
  public void showWarningMessageOnce(String text, String messageId, int timeoutMilliseconds) {
    // timeout or 'show once' is not implemented in this simple implementation, but this method
    // is overridden in MainView
    showWarningMessage(text);
  }

  public void showSuccessMessage(String text)
  {
    JOptionPane.showMessageDialog(parent, text, title, JOptionPane.PLAIN_MESSAGE);
  }

  public void showInfoMessage(String text)
  {
    JOptionPane.showMessageDialog(parent, text, title, JOptionPane.INFORMATION_MESSAGE);
  }

  public void showErrorMessage(Exception cause)
  {
    this.showErrorMessage(cause, "");
  }

  /**
   * Show a human-readable but useful message for an exception.
   * @param cause Exception
   * @param text Error message
   */
  public void showErrorMessage(Exception cause, String text)
  {
    cause.printStackTrace();
    String message = getHumanReadableErrorMessage(cause, text);
    JOptionPane.showMessageDialog(parent, message, title + " Error", JOptionPane.ERROR_MESSAGE);
  }
    /**
   * Generate a human-readable but useful message for an exception.
   * Also print the stack trace to stdout.
   * @param cause Exception
   * @param text Error message (optional)
   * @return Message string useful for showing in an error dialog
  */
    public static String getHumanReadableErrorMessage(Throwable cause, String text) {
      return getHumanReadableErrorMessage(cause, text, false);
    }

    /**
   * Generate a human-readable but useful message for an exception.
   * Also print the stack trace to stdout.
   * @param cause Exception
   * @param text Error message (optional)
   * @param alwaysShowStacktrace force that a stacktrace is shown (if false: stacktrace is hidden for well-known exceptions such as "Host not found")
   * @return Message string useful for showing in an error dialog
   */
  public static String getHumanReadableErrorMessage(Throwable cause, String text, boolean alwaysShowStacktrace)
  {
    cause.printStackTrace();
    String message = "";
    if (text != null && text.length() > 0)
    {
      message = text + "\n";
    }
    // display the localized message (such as "No route to host") if there is one
    // otherwise show the class name
    if (cause.getLocalizedMessage() == null)
    {
      message = message + cause.getClass().getSimpleName();
    }
    else
    {
      if (cause instanceof java.net.SocketException)
      {
        // Network errors like "port not found" have meaningful error messages
        message = message + cause.getLocalizedMessage();
      }
      else
      {
        // Most other exceptions are not easy to understand without the class name
        // (e.g. 'java.net.UnknownHostException: foo.example.com')
        message = message + cause.getClass().getSimpleName() + ": " + cause.getLocalizedMessage();
      }
    }
    // for interesting exceptions, add the first few stack trace lines
    boolean emptyMessage = cause.getMessage() == null || cause.getMessage().trim().length() == 0;
    if (alwaysShowStacktrace ||
        cause.getClass().equals(NullPointerException.class) ||
        cause.getClass().equals(ArrayIndexOutOfBoundsException.class) ||
        cause.getClass().equals(ClassCastException.class) ||
        (cause.getClass().equals(Exception.class) && emptyMessage))
    {
      StackTraceElement[] stackTrace = cause.getStackTrace();
      for (int i = 0; i < 2 && i < stackTrace.length; i++)
      {
        message = message + "\n" + stackTrace[i].toString();
      }
    }
    return message;
  }

  public static String getHumanReadableErrorMessage(Throwable cause) {
    return getHumanReadableErrorMessage(cause, null);
  }

  public void showErrorMessage(String text)
  {
    JOptionPane.showMessageDialog(parent, text, title + " Error", JOptionPane.ERROR_MESSAGE);
  }
  
  public void removeMessageWithId(String messageId) {
    // do nothing, needs to be implemented by subclass
  }

  private Double askUnit(UnitTextfield tf, String text, double val)
  {
    Box b = Box.createVerticalBox();
    b.add(new JLabel(text));
    b.add(tf);
    tf.setValue(val);
    if (JOptionPane.showConfirmDialog(parent, b, text, JOptionPane.OK_CANCEL_OPTION, JOptionPane.DEFAULT_OPTION) == JOptionPane.OK_OPTION)
    {
      return tf.getValue();
    }
    return null;
  }

  public Double askLength(String text, double mm)
  {
    return this.askUnit(new LengthTextfield(), text, mm);
  }

  public Double askAngle(String text, double rad)
  {
    return this.askUnit(new AngleTextfield(), text, rad);
  }
}
