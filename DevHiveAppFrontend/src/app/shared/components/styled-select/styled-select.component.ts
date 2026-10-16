import { Component, ElementRef, HostListener, input, output, signal } from '@angular/core';

export interface SelectOption<T extends string | number | null = string | number | null> {
  label: string;
  value: T;
}

@Component({
  selector: 'app-styled-select',
  templateUrl: './styled-select.component.html'
})
export class StyledSelectComponent<T extends string | number | null> {
  readonly id = input.required<string>();
  readonly label = input.required<string>();
  readonly options = input.required<readonly SelectOption<T>[]>();
  readonly value = input<T | null>(null);
  readonly placeholder = input('Select an option');
  readonly disabled = input(false);
  readonly valueChange = output<T>();
  readonly open = signal(false);
  readonly activeIndex = signal(0);

  constructor(private readonly element: ElementRef<HTMLElement>) {}

  get selectedOption(): SelectOption<T> | undefined {
    return this.options().find(option => option.value === this.value());
  }

  get activeOptionId(): string | null {
    return this.open() ? `${this.id()}-option-${this.activeIndex()}` : null;
  }

  toggle(): void {
    if (this.disabled()) return;
    if (this.open()) {
      this.open.set(false);
      return;
    }
    this.openMenu();
  }

  onKeydown(event: KeyboardEvent): void {
    if (this.disabled()) return;
    const options = this.options();
    if (!options.length) return;

    if (!this.open()) {
      if (event.key === 'ArrowDown' || event.key === 'ArrowUp' || event.key === 'Enter' || event.key === ' ') {
        event.preventDefault();
        this.openMenu(event.key === 'ArrowUp' ? -1 : 0);
      }
      return;
    }

    if (event.key === 'Escape') {
      event.preventDefault();
      this.open.set(false);
    } else if (event.key === 'ArrowDown' || event.key === 'ArrowUp') {
      event.preventDefault();
      const direction = event.key === 'ArrowDown' ? 1 : -1;
      this.activeIndex.update(index => (index + direction + options.length) % options.length);
    } else if (event.key === 'Home' || event.key === 'End') {
      event.preventDefault();
      this.activeIndex.set(event.key === 'Home' ? 0 : options.length - 1);
    } else if (event.key === 'Enter' || event.key === ' ') {
      event.preventDefault();
      this.choose(options[this.activeIndex()].value);
    }
  }

  choose(value: T): void {
    this.valueChange.emit(value);
    this.open.set(false);
  }

  @HostListener('document:click', ['$event'])
  closeOnOutsideClick(event: MouseEvent): void {
    if (event.target instanceof Node && !this.element.nativeElement.contains(event.target)) {
      this.open.set(false);
    }
  }

  @HostListener('document:focusin', ['$event'])
  closeOnOutsideFocus(event: FocusEvent): void {
    if (event.target instanceof Node && !this.element.nativeElement.contains(event.target)) {
      this.open.set(false);
    }
  }

  private openMenu(direction = 0): void {
    const options = this.options();
    if (!options.length) return;
    const selectedIndex = options.findIndex(option => option.value === this.value());
    const fallbackIndex = direction < 0 ? options.length - 1 : 0;
    this.activeIndex.set(selectedIndex >= 0 ? selectedIndex : fallbackIndex);
    this.open.set(true);
  }
}
