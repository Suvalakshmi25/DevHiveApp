import { Component, input, output } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { CodeSnippet, SnippetComment } from '../../../shared/models/workspace.models';

@Component({
  selector: 'app-snippet-library',
  imports: [FormsModule],
  templateUrl: './snippet-library.component.html'
})
export class SnippetLibraryComponent {
  readonly snippets = input.required<CodeSnippet[]>();
  readonly selectedProject = input(false);
  readonly canContribute = input(false);
  readonly snippetComments = input.required<Partial<Record<number, SnippetComment[]>>>();
  readonly commentDrafts = input.required<Record<number, { comment: string; lineNumber: number | null }>>();

  readonly create = output<void>();
  readonly toggleComments = output<CodeSnippet>();
  readonly addComment = output<{ snippetId: number; comment: string; lineNumber: number | null }>();

  submitComment(snippetId: number): void {
    const draft = this.commentDrafts()[snippetId];
    const comment = draft?.comment.trim();
    if (!draft || !comment) return;
    this.addComment.emit({ snippetId, comment, lineNumber: draft.lineNumber });
  }
}
