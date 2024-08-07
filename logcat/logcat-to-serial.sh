#!/bin/bash

# Function to check if adb is installed
check_adb() {
  if ! command -v adb &>/dev/null; then
    echo "Error: ADB (Android Debug Bridge) is not installed or not in PATH."
    exit 1
  fi
  echo "ADB is installed."
}

# Function to print adb logcat with filtering by tag and start/end characters
print_filtered_logcat() {
  echo "Printing filtered logcat..."

  # Start and end characters for filtering
  start_char=":"
  end_char="."
  
  # Filter by tag "Unity" and continuously read characters from the output
  adb logcat -s Unity | while IFS= read -r -n1 char; do
    if [[ $char == "$start_char" ]]; then
      filtered_output=""
      while IFS= read -r -n1 char; do
        if [[ $char == "$end_char" ]]; then
          echo "$filtered_output"
          # Print the filtered output to USB serial interface (replace /dev/ttyUSB0 with your actual serial interface)
          echo -n "$filtered_output" > /dev/cu.usbmodem143401
          break
        else
          filtered_output+="$char"
        fi
      done
    fi
  done
}

# Check if adb is installed
check_adb

echo -n "B90" > /dev/cu.usbmodem143401

# Print adb logcat with filtering and send to USB serial interface
print_filtered_logcat



