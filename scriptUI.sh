#!/bin/bash

# Check if whiptail is installed
if ! command -v whiptail &> /dev/null
then
    echo "Whiptail could not be found. Please install it first."
    exit
fi

# Define the paths to your scripts
ADMIN_SCRIPT="./admin.sh"
GET_ALL_MESSAGES_SCRIPT="./getAllMessages.sh"
DUPLICATES_SCRIPT="./duplicates.sh"
MESSAGE_ID_SCRIPT="./messageIdScript.sh"

# Check if scripts exist
if [[ ! -f "$ADMIN_SCRIPT" ]] || [[ ! -f "$GET_ALL_MESSAGES_SCRIPT" ]] || [[ ! -f "$DUPLICATES_SCRIPT" ]] || [[ ! -f "$MESSAGE_ID_SCRIPT" ]]; then
    echo "One or more scripts are missing. Please ensure all scripts are in the same directory."
    exit 1
fi

# Display a welcome message
whiptail --title "Elasticsearch Script Executor" --msgbox "Welcome to the Elasticsearch Script Executor!" 8 60

# Create a menu for selecting the script to execute
CHOICE=$(whiptail --title "Available Scripts" --menu "Choose a script to run:" 15 60 4 \
"1" "Run admin script" \
"2" "Run getAllMessages script" \
"3" "Run duplicates script" \
"4" "Run messageIdScript script" \
"5" "Exit" 3>&1 1>&2 2>&3)

# Process the user's choice
case $CHOICE in
    1)
        # Run admin script
        whiptail --title "Running Script" --msgbox "Running admin script..." 8 60
        bash "$ADMIN_SCRIPT"
        ;;
    2)
        # Run getAllMessages script
        whiptail --title "Running Script" --msgbox "Running getAllMessages script..." 8 60
        bash "$GET_ALL_MESSAGES_SCRIPT"
        ;;
    3)
        # Run duplicates script
        whiptail --title "Running Script" --msgbox "Running duplicates script..." 8 60
        bash "$DUPLICATES_SCRIPT"
        ;;
    4)
        # Run messageIdScript script
        whiptail --title "Running Script" --msgbox "Running messageIdScript script..." 8 60
        bash "$MESSAGE_ID_SCRIPT"
        ;;
    5)
        # Exit the script
        whiptail --title "Goodbye" --msgbox "Exiting the script. Goodbye!" 8 60
        exit 0
        ;;
    *)
        # Invalid choice
        whiptail --title "Error" --msgbox "Invalid choice. Please try again." 8 60
        ;;
esac

# Loop back to the menu after executing the chosen script
$0
