import { ComponentFixture, TestBed } from '@angular/core/testing';
import { StyledSelectComponent } from './styled-select.component';

describe('StyledSelectComponent', () => {
  let fixture: ComponentFixture<StyledSelectComponent<string>>;
  let component: StyledSelectComponent<string>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [StyledSelectComponent]
    }).compileComponents();

    fixture = TestBed.createComponent(StyledSelectComponent<string>);
    component = fixture.componentInstance;
    fixture.componentRef.setInput('id', 'test-select');
    fixture.componentRef.setInput('label', 'Test option');
    fixture.componentRef.setInput('options', [
      { label: 'First', value: 'first' },
      { label: 'Second', value: 'second' }
    ]);
    fixture.componentRef.setInput('value', 'first');
    fixture.detectChanges();
  });

  it('shows the selected option', () => {
    expect(fixture.nativeElement.querySelector('.styled-select-value')?.textContent.trim()).toBe('First');
  });

  it('supports keyboard selection and emits the selected value', () => {
    const selectedValues: string[] = [];
    component.valueChange.subscribe(value => selectedValues.push(value));
    const trigger = fixture.nativeElement.querySelector('button') as HTMLButtonElement;

    trigger.dispatchEvent(new KeyboardEvent('keydown', { key: 'ArrowDown', bubbles: true }));
    fixture.detectChanges();
    trigger.dispatchEvent(new KeyboardEvent('keydown', { key: 'ArrowDown', bubbles: true }));
    trigger.dispatchEvent(new KeyboardEvent('keydown', { key: 'Enter', bubbles: true }));
    fixture.detectChanges();

    expect(selectedValues).toEqual(['second']);
    expect(component.open()).toBe(false);
  });

  it('closes when clicking outside the menu', () => {
    component.toggle();
    fixture.detectChanges();
    document.body.dispatchEvent(new MouseEvent('click', { bubbles: true }));
    expect(component.open()).toBe(false);
  });
});
