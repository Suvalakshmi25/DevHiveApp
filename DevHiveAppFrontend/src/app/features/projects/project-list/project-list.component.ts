import { Component, input, output } from '@angular/core';
import { Project } from '../../../shared/models/workspace.models';

@Component({
  selector: 'app-project-list',
  templateUrl: './project-list.component.html'
})
export class ProjectListComponent {
  readonly projects = input.required<Project[]>();
  readonly selectedProjectId = input<number | null>(null);
  readonly selected = output<Project>();
}
