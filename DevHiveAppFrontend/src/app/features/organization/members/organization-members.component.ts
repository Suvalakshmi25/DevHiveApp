import { Component, input, output } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Member, MemberRole } from '../../../shared/models/workspace.models';

@Component({
  selector: 'app-organization-members',
  imports: [FormsModule],
  templateUrl: './organization-members.component.html'
})
export class OrganizationMembersComponent {
  readonly members = input.required<Member[]>();
  readonly roles = input.required<MemberRole[]>();
  readonly canAdminister = input(false);

  readonly refresh = output<void>();
  readonly invite = output<{ email: string; role: MemberRole }>();
  readonly roleChange = output<{ member: Member; role: MemberRole }>();
  readonly remove = output<Member>();

  email = '';
  role: MemberRole = 'DEVELOPER';

  inviteMember(): void {
    const email = this.email.trim();
    if (!email) return;
    this.invite.emit({ email, role: this.role });
    this.email = '';
    this.role = 'DEVELOPER';
  }
}
