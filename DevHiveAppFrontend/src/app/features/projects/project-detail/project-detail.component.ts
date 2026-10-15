import { Component, input, output } from '@angular/core';
import { Project } from '../../../shared/models/workspace.models';

@Component({
  selector: 'app-project-detail',
  templateUrl: './project-detail.component.html'
})
export class ProjectDetailComponent {
  readonly project = input<Project | null>(null);
  readonly organizationSelected = input(false);
  readonly projectsCount = input(0);
  readonly canAdminister = input(false);
  readonly canContribute = input(false);

  readonly edit = output<void>();
  readonly delete = output<void>();
  readonly refresh = output<void>();
  readonly addTask = output<void>();
  readonly createProject = output<void>();
  readonly createOrganization = output<void>();
}
