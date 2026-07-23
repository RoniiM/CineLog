import { Directive, HostListener, input, output } from '@angular/core';

@Directive({
  selector: '[appInfiniteScroll]'
})
export class InfiniteScrollDirective {
  scrollDistance = input(200);
  disabled = input(false);
  scrolled = output<void>();

  private emitted = false;

  @HostListener('window:scroll')
  onScroll(): void {
    if (this.disabled()) {
      return;
    }

    const scrollPosition = window.innerHeight + window.scrollY;
    const threshold = document.documentElement.scrollHeight - this.scrollDistance();

    if (scrollPosition < threshold) {
      this.emitted = false;
      return;
    }

    if (!this.emitted) {
      this.emitted = true;
      this.scrolled.emit();
    }
  }
}
