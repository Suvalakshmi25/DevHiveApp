import { Component, effect, input, output } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Member, MemberRole } from '../../../shared/models/workspace.models';
import { SelectOption, StyledSelectComponent } from '../../../shared/components/styled-select/styled-select.component';

@Component({
  selector: 'app-organization-members',
  imports: [FormsModule, StyledSelectComponent],
  templateUrl: './organization-members.component.html'
})
export class OrganizationMembersComponent {
  readonly members = input.required<Member[]>();
  readonly roleOptions = input.required<readonly SelectOption<MemberRole>[]>();
  readonly canAdminister = input(false);
  readonly busyMemberIds = input<number[]>([]);
  readonly inviteStatus = input<'idle' | 'pending' | 'success'>('idle');

  readonly refresh = output<void>();
  readonly invite = output<{ email: string; role: MemberRole }>();
  readonly roleChange = output<{ member: Member; role: MemberRole }>();
  readonly remove = output<Member>();

  email = '';
  role: MemberRole = 'DEVELOPER';

  constructor() {
    effect(() => {
      if (this.inviteStatus() === 'success') {
        this.email = '';
        this.role = 'DEVELOPER';
      }
    });
  }

  inviteMember(): void {
    const email = this.email.trim();
    if (!email || this.inviteStatus() === 'pending') return;
    this.invite.emit({ email, role: this.role });
  }
}
