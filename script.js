document.getElementById('year').textContent = new Date().getFullYear();
const links = [...document.querySelectorAll('.dock a')];
const sections = links.map(link => document.querySelector(link.getAttribute('href'))).filter(Boolean);
const observer = new IntersectionObserver(entries => {
  entries.forEach(entry => {
    if (!entry.isIntersecting) return;
    links.forEach(link => link.classList.toggle('active', link.getAttribute('href') === '#' + entry.target.id));
  });
}, { rootMargin: '-25% 0px -55% 0px' });
sections.forEach(section => observer.observe(section));
