import sqlite3
import tkinter as tk
from tkinter import ttk, messagebox, filedialog
from tkinter.scrolledtext import ScrolledText

class ThemeManager:
    def __init__(self):
        self.themes = {
            "Dark": {
                "bg": "#2d2d2d",
                "fg": "white",
                "entry_bg": "#3d3d3d",
                "entry_fg": "white",
                "button_bg": "#4CAF50",
                "button_fg": "white",
                "tree_bg": "#3d3d3d",
                "tree_fg": "white",
                "tree_heading_bg": "#4d4d4d",
                "tree_heading_fg": "white",
            },
            "Light": {
                "bg": "#f5f5f5",
                "fg": "black",
                "entry_bg": "white",
                "entry_fg": "black",
                "button_bg": "#4CAF50",
                "button_fg": "white",
                "tree_bg": "white",
                "tree_fg": "black",
                "tree_heading_bg": "#e0e0e0",
                "tree_heading_fg": "black",
            },
        }
        self.current_theme = "Dark"  # Default theme

    def get_theme(self):
        return self.themes[self.current_theme]

    def set_theme(self, theme_name):
        if theme_name in self.themes:
            self.current_theme = theme_name
        else:
            raise ValueError(f"Theme '{theme_name}' not found.")

class NoteManager:
    def __init__(self, db_name="notes.db"):
        self.conn = sqlite3.connect(db_name)
        self.create_table()

    def create_table(self):
        with self.conn:
            self.conn.execute(
                "CREATE TABLE IF NOT EXISTS notes (id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT NOT NULL, content TEXT NOT NULL)"
            )

    def list_notes(self):
        with self.conn:
            cursor = self.conn.execute("SELECT id, name, content FROM notes")
            notes = cursor.fetchall()
            return notes

    def add_note(self, name, content):
        with self.conn:
            self.conn.execute("INSERT INTO notes (name, content) VALUES (?, ?)", (name, content))
            return "Note added!"

    def update_note(self, note_id, name, content):
        with self.conn:
            self.conn.execute("UPDATE notes SET name = ?, content = ? WHERE id = ?", (name, content, note_id))
            return "Note updated!"

    def delete_note_by_id(self, note_id):
        with self.conn:
            cursor = self.conn.execute("DELETE FROM notes WHERE id = ?", (note_id,))
            if cursor.rowcount == 0:
                return "Invalid note ID!"
            return "Note deleted!"

    def search_notes(self, query):
        with self.conn:
            cursor = self.conn.execute("SELECT id, name, content FROM notes WHERE name LIKE ? OR content LIKE ?", ('%' + query + '%', '%' + query + '%'))
            results = cursor.fetchall()
            return results

    def load_notes_from_text(self, text):
        notes = [note.strip() for note in text.split('===') if note.strip()]
        with self.conn:
            for note in notes:
                if '\n' in note:
                    name, content = note.split('\n', 1)
                else:
                    name, content = "Untitled", note
                self.conn.execute("INSERT INTO notes (name, content) VALUES (?, ?)", (name.strip(), content.strip()))
        return f"Loaded {len(notes)} notes."

class NoteApp:
    def __init__(self, root):
        self.manager = NoteManager()
        self.theme_manager = ThemeManager()
        self.root = root
        self.root.title("Note Manager")
        self.root.geometry("1000x700")
        self.apply_theme()

        # Theme Selection Menu
        self.theme_var = tk.StringVar(value=self.theme_manager.current_theme)
        self.theme_menu = ttk.Combobox(root, textvariable=self.theme_var, values=list(self.theme_manager.themes.keys()))
        self.theme_menu.grid(row=0, column=2, padx=10, pady=10, sticky="e")
        self.theme_menu.bind("<<ComboboxSelected>>", self.change_theme)

        # UI Elements
        self.create_widgets()

    def create_widgets(self):
        # Header Frame
        self.header_frame = tk.Frame(self.root, bg=self.theme_manager.get_theme()["bg"])
        self.header_frame.grid(row=0, column=0, columnspan=3, sticky="ew", padx=10, pady=10)

        self.header_label = tk.Label(self.header_frame, text="Note Manager", font=("Arial", 16, "bold"), bg="#4CAF50", fg="white")
        self.header_label.pack(pady=10)

        # Input Frame
        self.input_frame = tk.Frame(self.root, bg=self.theme_manager.get_theme()["bg"])
        self.input_frame.grid(row=1, column=0, columnspan=3, padx=10, pady=10, sticky="ew")

        self.name_label = tk.Label(self.input_frame, text="Note Name:", font=("Arial", 10), bg=self.theme_manager.get_theme()["bg"], fg=self.theme_manager.get_theme()["fg"])
        self.name_label.grid(row=0, column=0, padx=5, pady=5, sticky="w")

        self.name_entry = ttk.Entry(self.input_frame, width=50)
        self.name_entry.grid(row=0, column=1, padx=5, pady=5, sticky="ew")

        self.content_label = tk.Label(self.input_frame, text="Note Content:", font=("Arial", 10), bg=self.theme_manager.get_theme()["bg"], fg=self.theme_manager.get_theme()["fg"])
        self.content_label.grid(row=1, column=0, padx=5, pady=5, sticky="w")

        self.content_text = ScrolledText(self.input_frame, height=10, width=60, font=("Arial", 10), wrap=tk.WORD, bg=self.theme_manager.get_theme()["entry_bg"], fg=self.theme_manager.get_theme()["entry_fg"])
        self.content_text.grid(row=1, column=1, padx=5, pady=5, sticky="ew")

        self.add_button = ttk.Button(self.input_frame, text="Add Note", command=self.add_note)
        self.add_button.grid(row=2, column=1, padx=5, pady=5, sticky="e")

        # Search Frame
        self.search_frame = tk.Frame(self.root, bg=self.theme_manager.get_theme()["bg"])
        self.search_frame.grid(row=2, column=0, columnspan=3, padx=10, pady=10, sticky="ew")

        self.search_label = tk.Label(self.search_frame, text="Search:", font=("Arial", 10), bg=self.theme_manager.get_theme()["bg"], fg=self.theme_manager.get_theme()["fg"])
        self.search_label.grid(row=0, column=0, padx=5, pady=5, sticky="w")

        self.search_entry = ttk.Entry(self.search_frame, width=50)
        self.search_entry.grid(row=0, column=1, padx=5, pady=5, sticky="ew")
        self.search_entry.bind("<Return>", lambda e: self.search_notes())  # Search on Enter key

        self.search_button = ttk.Button(self.search_frame, text="🔍", command=self.search_notes)
        self.search_button.grid(row=0, column=2, padx=5, pady=5, sticky="w")

        # Table Frame
        self.table_frame = tk.Frame(self.root, bg=self.theme_manager.get_theme()["bg"])
        self.table_frame.grid(row=3, column=0, columnspan=3, padx=10, pady=10, sticky="nsew")

        self.tree = ttk.Treeview(self.table_frame, columns=("ID", "Name", "Content", "Copy"), show="headings", selectmode="browse")
        self.tree.heading("ID", text="ID")
        self.tree.heading("Name", text="Name")
        self.tree.heading("Content", text="Content")
        self.tree.heading("Copy", text="📋")
        self.tree.column("ID", width=50, anchor="center")
        self.tree.column("Name", width=150, anchor="w")
        self.tree.column("Content", width=500, anchor="w")
        self.tree.column("Copy", width=50, anchor="center")
        self.tree.pack(fill=tk.BOTH, expand=True)

        # Bind double-click to edit
        self.tree.bind("<Double-1>", self.edit_note)

        # Bind clipboard button in table
        self.tree.bind("<Button-1>", self.handle_table_click)

        # Action Buttons
        self.action_frame = tk.Frame(self.root, bg=self.theme_manager.get_theme()["bg"])
        self.action_frame.grid(row=4, column=0, columnspan=3, padx=10, pady=10, sticky="ew")

        self.load_button = ttk.Button(self.action_frame, text="Load Notes", command=self.load_notes)
        self.load_button.pack(side=tk.LEFT, padx=5)

        self.delete_button = ttk.Button(self.action_frame, text="Delete Note", command=self.delete_note)
        self.delete_button.pack(side=tk.LEFT, padx=5)

        # Configure grid weights
        self.root.grid_rowconfigure(3, weight=1)
        self.root.grid_columnconfigure(1, weight=1)

    def apply_theme(self):
        theme = self.theme_manager.get_theme()
        self.root.configure(bg=theme["bg"])
        for widget in self.root.winfo_children():
            self.update_widget_theme(widget, theme)

    def update_widget_theme(self, widget, theme):
        if isinstance(widget, (tk.Frame, ttk.Frame)):
            widget.configure(bg=theme["bg"])
        elif isinstance(widget, (tk.Label, ttk.Label)):
            widget.configure(bg=theme["bg"], fg=theme["fg"])
        elif isinstance(widget, (tk.Entry, ttk.Entry)):
            widget.configure(background=theme["entry_bg"], foreground=theme["entry_fg"])
        elif isinstance(widget, (tk.Button, ttk.Button)):
            widget.configure(background=theme["button_bg"], foreground=theme["button_fg"])
        elif isinstance(widget, ttk.Treeview):
            widget.configure(style="Treeview")
            self.style.configure("Treeview", background=theme["tree_bg"], foreground=theme["tree_fg"], fieldbackground=theme["tree_bg"])
            self.style.configure("Treeview.Heading", background=theme["tree_heading_bg"], foreground=theme["tree_heading_fg"])
        for child in widget.winfo_children():
            self.update_widget_theme(child, theme)

    def change_theme(self, event=None):
        selected_theme = self.theme_var.get()
        self.theme_manager.set_theme(selected_theme)
        self.apply_theme()

    def add_note(self):
        name = self.name_entry.get().strip()
        content = self.content_text.get("1.0", tk.END).strip()
        if name and content:
            message = self.manager.add_note(name, content)
            messagebox.showinfo("Info", message)
            self.name_entry.delete(0, tk.END)
            self.content_text.delete("1.0", tk.END)
            self.list_notes()

    def delete_note(self):
        selected_item = self.tree.selection()
        if selected_item:
            note_id = self.tree.item(selected_item, "values")[0]
            message = self.manager.delete_note_by_id(note_id)
            messagebox.showinfo("Info", message)
            self.list_notes()

    def search_notes(self):
        query = self.search_entry.get().strip()
        results = self.manager.search_notes(query)
        self.update_table(results)

    def load_notes(self):
        load_window = tk.Toplevel(self.root)
        load_window.title("Load Notes")
        load_window.geometry("600x400")
        load_window.configure(bg=self.theme_manager.get_theme()["bg"])

        text_area = ScrolledText(load_window, height=20, width=70, wrap=tk.WORD, font=("Arial", 10), bg=self.theme_manager.get_theme()["entry_bg"], fg=self.theme_manager.get_theme()["entry_fg"])
        text_area.pack(padx=10, pady=10, fill=tk.BOTH, expand=True)

        def save_notes():
            text = text_area.get("1.0", tk.END).strip()
            if text:
                message = self.manager.load_notes_from_text(text)
                messagebox.showinfo("Info", message)
                load_window.destroy()
                self.list_notes()

        save_button = ttk.Button(load_window, text="Save Notes", command=save_notes)
        save_button.pack(pady=10)

    def list_notes(self):
        notes = self.manager.list_notes()
        self.update_table(notes)

    def update_table(self, notes):
        self.tree.delete(*self.tree.get_children())
        for note in notes:
            note_id, name, content = note
            self.tree.insert("", tk.END, values=(note_id, name, content, "📋"))

    def edit_note(self, event):
        selected_item = self.tree.selection()
        if selected_item:
            note_id, name, content, _ = self.tree.item(selected_item, "values")
            self.open_edit_window(note_id, name, content)

    def open_edit_window(self, note_id, name, content):
        edit_window = tk.Toplevel(self.root)
        edit_window.title("Edit Note")
        edit_window.geometry("600x400")
        edit_window.configure(bg=self.theme_manager.get_theme()["bg"])

        name_label = tk.Label(edit_window, text="Note Name:", font=("Arial", 10), bg=self.theme_manager.get_theme()["bg"], fg=self.theme_manager.get_theme()["fg"])
        name_label.pack(padx=10, pady=5, anchor="w")

        name_entry = ttk.Entry(edit_window, width=50)
        name_entry.pack(padx=10, pady=5, fill=tk.X)
        name_entry.insert(0, name)

        content_label = tk.Label(edit_window, text="Note Content:", font=("Arial", 10), bg=self.theme_manager.get_theme()["bg"], fg=self.theme_manager.get_theme()["fg"])
        content_label.pack(padx=10, pady=5, anchor="w")

        content_text = ScrolledText(edit_window, height=10, width=60, font=("Arial", 10), wrap=tk.WORD, bg=self.theme_manager.get_theme()["entry_bg"], fg=self.theme_manager.get_theme()["entry_fg"])
        content_text.pack(padx=10, pady=5, fill=tk.BOTH, expand=True)
        content_text.insert("1.0", content)

        def save_changes():
            new_name = name_entry.get().strip()
            new_content = content_text.get("1.0", tk.END).strip()
            if new_name and new_content:
                message = self.manager.update_note(note_id, new_name, new_content)
                messagebox.showinfo("Info", message)
                edit_window.destroy()
                self.list_notes()

        save_button = ttk.Button(edit_window, text="Save", command=save_changes)
        save_button.pack(pady=10)

        def copy_note():
            self.root.clipboard_clear()
            self.root.clipboard_append(content_text.get("1.0", tk.END).strip())
            self.show_auto_dismiss_dialog("Note content copied to clipboard!")

        copy_button = ttk.Button(edit_window, text="Copy", command=copy_note)
        copy_button.pack(pady=5)

    def handle_table_click(self, event):
        region = self.tree.identify_region(event.x, event.y)
        if region == "cell":
            column = self.tree.identify_column(event.x)
            if column == "#4":  # Copy column
                item = self.tree.identify_row(event.y)
                note_content = self.tree.item(item, "values")[2]
                self.root.clipboard_clear()
                self.root.clipboard_append(note_content)
                self.show_auto_dismiss_dialog("Note content copied to clipboard!")

    def show_auto_dismiss_dialog(self, message):
        dialog = tk.Toplevel(self.root)
        dialog.title("Info")
        dialog.geometry("300x50")
        dialog.attributes("-topmost", True)
        dialog.configure(bg=self.theme_manager.get_theme()["bg"])
        label = tk.Label(dialog, text=message, font=("Arial", 10), bg=self.theme_manager.get_theme()["bg"], fg=self.theme_manager.get_theme()["fg"])
        label.pack(pady=10)
        # Auto-dismiss after 2 seconds
        self.root.after(2000, dialog.destroy)

def main():
    root = tk.Tk()
    app = NoteApp(root)
    root.mainloop()

if __name__ == "__main__":
    main()