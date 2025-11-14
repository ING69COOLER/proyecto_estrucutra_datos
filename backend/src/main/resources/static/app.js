// ============================================
// SISTEMA DE GESTIÓN DE DESASTRES - FRONTEND
// Con sistema de roles (Administrador/Operador)
// ============================================

const apiBase = 'http://localhost:8081/api';
const wsBase = 'ws://localhost:8081/ws';

// Variables globales
let currentUser = {
  id: null,
  nombre: '',
  email: '',
  rol: ''
};

let map = null;
let zonasLayer = null;
let rutasLayer = null;
let stompClient = null;

// ============================================
// INICIALIZACIÓN
// ============================================
window.addEventListener('load', async () => {
  console.log('Iniciando aplicación...');
  
  // 1. Cargar información del usuario
  await loadUserInfo();
  
  // 2. Configurar UI según rol
  configureUIByRole();
  
  // 3. Inicializar mapa
  initMap();
  
  // 4. Configurar navegación de pestañas
  setupTabNavigation();
  
  // 5. Configurar event listeners
  setupEventListeners();
  
  // 6. Cargar datos iniciales
  loadRecursos();
  loadZonas();
  loadUbicaciones();
  
  // 7. Conectar WebSocket
  try {
    connectWebSocket();
  } catch (e) {
    console.warn('Error iniciando WebSocket:', e);
  }
  
  console.log('Aplicación iniciada correctamente');
});

// ============================================
// AUTENTICACIÓN Y SESIÓN
// ============================================
async function loadUserInfo() {
  try {
    const response = await fetch(apiBase + '/session/user');
    
    if (!response.ok) {
      console.error('No hay sesión activa, redirigiendo a login...');
      window.location.href = '/login';
      return;
    }
    
    const userData = await response.json();
    currentUser = userData;
    
    console.log('Usuario autenticado:', currentUser);
    
    // Actualizar UI con información del usuario
    updateUserDisplay();
    
    addLog(`Usuario ${userData.nombre} (${userData.rol}) ha iniciado sesión`);
  } catch (error) {
    console.error('Error cargando información del usuario:', error);
    window.location.href = '/login';
  }
}

function updateUserDisplay() {
  // Actualizar elementos del navbar y sidebar
  const userNameEl = document.getElementById('userName');
  const userRoleEl = document.getElementById('userRole');
  const sidebarUserName = document.getElementById('sidebarUserName');
  const sidebarUserId = document.getElementById('sidebarUserId');
  const sidebarUserRole = document.getElementById('sidebarUserRole');
  
  if (userNameEl) userNameEl.textContent = currentUser.nombre;
  if (userRoleEl) userRoleEl.textContent = currentUser.rol;
  if (sidebarUserName) sidebarUserName.textContent = currentUser.nombre;
  if (sidebarUserId) sidebarUserId.textContent = currentUser.id;
  if (sidebarUserRole) sidebarUserRole.textContent = currentUser.rol;
}

// ============================================
// CONFIGURACIÓN DE PERMISOS POR ROL
// ============================================
function configureUIByRole() {
  const isAdmin = currentUser.rol === 'ADMINISTRADOR';
  
  console.log(`Configurando UI para rol: ${currentUser.rol}`);
  
  // Obtener todos los elementos que requieren permisos de admin
  const adminElements = document.querySelectorAll('.admin-only, [data-tab="panel-admin"], [data-tab="carga-datos"]');
  
  adminElements.forEach(el => {
    if (isAdmin) {
      el.style.display = '';
      el.classList.remove('d-none');
    } else {
      el.style.display = 'none';
      el.classList.add('d-none');
    }
  });
  
  // Actualizar badge de usuario según rol
  const userBadge = document.getElementById('userBadge');
  if (userBadge) {
    if (isAdmin) {
      userBadge.style.backgroundColor = '#dc3545'; // Rojo para admin
    } else {
      userBadge.style.backgroundColor = '#0d6efd'; // Azul para operador
    }
  }
  
  // Deshabilitar funciones de admin en formularios si es operador
  if (!isAdmin) {
    disableAdminForms();
  }
  
  addLog(`UI configurada para ${isAdmin ? 'Administrador' : 'Operador'}`);
}

function disableAdminForms() {
  // Deshabilitar formularios de creación de recursos, equipos, etc.
  const adminForms = [
    'form-create-resource',
    'form-create-team',
    'form-create-ubicacion'
  ];
  
  adminForms.forEach(formId => {
    const form = document.getElementById(formId);
    if (form) {
      const inputs = form.querySelectorAll('input, select, button');
      inputs.forEach(input => {
        input.disabled = true;
      });
    }
  });
  
  // Deshabilitar botones de carga de datos
  const adminButtons = [
    'btn-load-sample',
    'btn-load-file',
    'btn-clear-data',
    'btn-create-team'
  ];
  
  adminButtons.forEach(btnId => {
    const btn = document.getElementById(btnId);
    if (btn) {
      btn.disabled = true;
      btn.title = 'Requiere permisos de Administrador';
    }
  });
}

// ============================================
// NAVEGACIÓN DE PESTAÑAS
// ============================================
function setupTabNavigation() {
  document.querySelectorAll('.list-group .list-group-item[data-tab]').forEach(a => {
    a.addEventListener('click', (e) => {
      e.preventDefault();
      
      const tab = a.getAttribute('data-tab');
      
      // Verificar permisos para tabs de admin
      if ((tab === 'panel-admin' || tab === 'carga-datos') && currentUser.rol !== 'ADMINISTRADOR') {
        addLog('Acceso denegado: Se requieren permisos de Administrador');
        Swal.fire({
          icon: 'error',
          title: 'Acceso Denegado',
          text: 'No tienes permisos para acceder a esta sección'
        });
        return;
      }
      
      // Cambiar tab activo
      document.querySelectorAll('.list-group .list-group-item').forEach(x => x.classList.remove('active'));
      a.classList.add('active');
      
      // Mostrar sección correspondiente
      document.querySelectorAll('.tab-section').forEach(s => s.classList.remove('active'));
      const section = document.getElementById(tab);
      if (section) {
        section.classList.add('active');
      }
    });
  });
}

// ============================================
// EVENT LISTENERS
// ============================================
function setupEventListeners() {
  // Logout
  document.getElementById('menu-logout')?.addEventListener('click', (e) => {
    e.preventDefault();
    logout();
  });
  
  document.getElementById('menu-exit')?.addEventListener('click', (e) => {
    e.preventDefault();
    logout();
  });
  
  // Refrescar datos
  document.getElementById('btn-refresh')?.addEventListener('click', () => {
    loadRecursos();
    loadZonas();
    loadUbicaciones();
    addLog('Datos refrescados manualmente');
  });
  
  // Formularios (solo si es admin)
  if (currentUser.rol === 'ADMINISTRADOR') {
    setupAdminForms();
  }
  
  // Formularios disponibles para todos
  setupCommonForms();
  
  // Filtros
  document.getElementById('btn-apply-filters')?.addEventListener('click', applyFilters);
}

function setupAdminForms() {
  // Crear recurso
  document.getElementById('form-create-resource')?.addEventListener('submit', async (e) => {
    e.preventDefault();
    
    const recurso = {
      nombre: document.getElementById('r_nombre').value,
      tipo: document.getElementById('r_tipo').value,
      cantidad: parseInt(document.getElementById('r_cantidad').value),
      disponible: true,
      creadoPor: currentUser.id // Agregar referencia del usuario
    };
    
    try {
      const response = await fetch(apiBase + '/recursos', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(recurso)
      });
      
      if (response.ok) {
        const data = await response.json();
        addLog(`Recurso creado por ${currentUser.nombre}: ${data.nombre}`);
        loadRecursos();
        e.target.reset();
        Swal.fire('Éxito', 'Recurso creado correctamente', 'success');
      }
    } catch (error) {
      console.error('Error creando recurso:', error);
      addLog('Error creando recurso');
    }
  });
  
  // Crear ubicación
  document.getElementById('form-create-ubicacion')?.addEventListener('submit', async (e) => {
    e.preventDefault();
    
    const ubicacion = {
      nombre: document.getElementById('u_nombre').value,
      tipo: document.getElementById('u_tipo').value,
      lat: parseFloat(document.getElementById('u_lat').value),
      lng: parseFloat(document.getElementById('u_lng').value),
      nivelRiesgo: document.getElementById('u_nivelRiesgo').value,
      personasAfectadas: 0,
      creadoPor: currentUser.id // Agregar referencia del usuario
    };
    
    try {
      const response = await fetch(apiBase + '/zonas', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(ubicacion)
      });
      
      const msg = await response.text();
      
      if (response.status === 409) {
        Swal.fire('Duplicado', msg, 'warning');
      } else if (!response.ok) {
        Swal.fire('Error', 'No se pudo guardar la ubicación', 'error');
      } else {
        const data = JSON.parse(msg);
        addLog(`Ubicación creada por ${currentUser.nombre}: ${data.nombre}`);
        loadZonas();
        loadUbicaciones();
        e.target.reset();
        Swal.fire('Éxito', 'Ubicación creada correctamente', 'success');
      }
    } catch (error) {
      console.error('Error:', error);
      Swal.fire('Error', 'Error al crear la ubicación: ' + error.message, 'error');
    }
  });
  
  // Cargar datos de muestra
  document.getElementById('btn-load-sample')?.addEventListener('click', async () => {
    try {
      const response = await fetch(apiBase + '/admin/cargar-muestra', {
        method: 'POST'
      });
      
      if (response.ok) {
        const msg = await response.text();
        addLog(`${currentUser.nombre} cargó datos de muestra`);
        loadZonas();
        loadRecursos();
        loadUbicaciones();
        Swal.fire('Éxito', msg, 'success');
      } else if (response.status === 403) {
        Swal.fire('Acceso Denegado', 'No tienes permisos de Administrador', 'error');
      }
    } catch (error) {
      console.error('Error:', error);
      addLog('Error cargando datos de muestra');
    }
  });

  // Limpiar datos
  document.getElementById('btn-clear-data')?.addEventListener('click', async () => {
    const result = await Swal.fire({
      title: '¿Estás seguro?',
      text: 'Esto eliminará TODOS los datos de la base de datos',
      icon: 'warning',
      showCancelButton: true,
      confirmButtonColor: '#d33',
      cancelButtonColor: '#3085d6',
      confirmButtonText: 'Sí, eliminar todo',
      cancelButtonText: 'Cancelar'
    });

    if (result.isConfirmed) {
      try {
        const response = await fetch(apiBase + '/admin/limpiar-datos', {
          method: 'DELETE'
        });
        
        if (response.ok) {
          const msg = await response.text();
          addLog(`${currentUser.nombre} limpió todos los datos`);
          loadZonas();
          loadRecursos();
          loadUbicaciones();
          Swal.fire('Eliminado', msg, 'success');
        } else if (response.status === 403) {
          Swal.fire('Acceso Denegado', 'No tienes permisos de Administrador', 'error');
        }
      } catch (error) {
        console.error('Error:', error);
        Swal.fire('Error', 'Error al limpiar datos', 'error');
      }
    }
  });
}

function setupCommonForms() {
  // Calcular ruta (disponible para todos)
  document.getElementById('form-calc-route')?.addEventListener('submit', async (e) => {
    e.preventDefault();
    
    const origen = document.getElementById('route_origen').value;
    const destino = document.getElementById('route_destino').value;
    const modo = document.getElementById('route_mode').value;
    
    addLog(`${currentUser.nombre} calculó ruta de ${origen} a ${destino} (${modo})`);
    
    // Aquí implementarías la lógica de cálculo de ruta
    Swal.fire('Info', 'Función de cálculo de ruta en desarrollo', 'info');
  });
}

function logout() {
  Swal.fire({
    title: '¿Cerrar sesión?',
    text: '¿Estás seguro de que deseas salir?',
    icon: 'question',
    showCancelButton: true,
    confirmButtonText: 'Sí, salir',
    cancelButtonText: 'Cancelar'
  }).then((result) => {
    if (result.isConfirmed) {
      window.location.href = '/logout';
    }
  });
}

// ============================================
// MAPA
// ============================================
function initMap() {
  map = L.map('map').setView([4.5709, -74.2973], 6);
  
  L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
    attribution: '&copy; OpenStreetMap contributors'
  }).addTo(map);
  
  zonasLayer = L.layerGroup().addTo(map);
  rutasLayer = L.layerGroup().addTo(map);
}

// ============================================
// CARGA DE DATOS
// ============================================
async function loadRecursos() {
  try {
    const response = await fetch(apiBase + '/recursos');
    const list = await response.json();
    
    const tbody = document.querySelector('#tabla-recursos tbody');
    if (!tbody) return;
    
    tbody.innerHTML = '';
    let total = 0;
    
    list.forEach(r => {
      total += r.cantidad || 0;
      const tr = document.createElement('tr');
      tr.innerHTML = `
        <td>${r.nombre}</td>
        <td>${r.tipo}</td>
        <td>${r.cantidad}</td>
        <td>${r.disponible ? 'Sí' : 'No'}</td>
        <td><button class="btn btn-sm btn-outline-primary" data-id="${r.id}">Asignar</button></td>
      `;
      tbody.appendChild(tr);
    });
    
    const totalResourcesEl = document.getElementById('totalResources');
    if (totalResourcesEl) {
      totalResourcesEl.textContent = total;
    }
  } catch (error) {
    console.error('Error cargando recursos:', error);
  }
}

async function loadZonas() {
  try {
    const response = await fetch(apiBase + '/zonas');
    const list = await response.json();
    
    const tbody = document.querySelector('#tabla-zonas tbody');
    if (!tbody) return;
    
    tbody.innerHTML = '';
    if (zonasLayer) zonasLayer.clearLayers();
    
    let criticalCount = 0;
    
    list.forEach(z => {
      if (z.nivelRiesgo === 'CRITICO' || z.nivelRiesgo === 'ALTO') {
        criticalCount++;
      }
      
      const tr = document.createElement('tr');
      tr.innerHTML = `
        <td>${z.nombre}</td>
        <td>${z.tipo}</td>
        <td><span class="badge ${getBadgeClass(z.nivelRiesgo)}">${z.nivelRiesgo}</span></td>
        <td>${z.personasAfectadas}</td>
        <td>${z.recursosNecesarios?.length || 0}</td>
        <td>${new Date(z.updatedAt || Date.now()).toLocaleString()}</td>
        <td><button class="btn btn-sm btn-outline-secondary" data-id="${z.id}">Ver</button></td>
      `;
      tbody.appendChild(tr);
      
      // Agregar al mapa
      if (z.lat && z.lng && zonasLayer) {
        const marker = L.circleMarker([z.lat, z.lng], {
          radius: 8,
          color: getMarkerColor(z.nivelRiesgo)
        }).bindPopup(`
          <strong>${z.nombre}</strong><br/>
          Riesgo: ${z.nivelRiesgo}<br/>
          Personas: ${z.personasAfectadas}
        `);
        
        marker.on('click', () => {
          document.getElementById('dz_nombre').textContent = z.nombre;
          document.getElementById('dz_riesgo').textContent = z.nivelRiesgo;
          document.getElementById('dz_personas').textContent = z.personasAfectadas;
          document.getElementById('dz_recursos').textContent = 
            (z.recursosNecesarios || []).map(rr => rr.tipo).join(', ');
        });
        
        zonasLayer.addLayer(marker);
      }
    });
    
    // Actualizar contador de zonas críticas
    const countCriticalEl = document.getElementById('countCritical');
    if (countCriticalEl) {
      countCriticalEl.textContent = criticalCount;
    }
    
    // Event listener para botones "Ver"
    tbody.querySelectorAll('button[data-id]').forEach(btn => {
      btn.addEventListener('click', async () => {
        const id = btn.dataset.id;
        await mostrarDetalleUbicacion(id);
      });
    });
    
  } catch (error) {
    console.error('Error cargando zonas:', error);
  }
}

async function loadUbicaciones() {
  try {
    const response = await fetch(apiBase + '/zonas');
    const ubicaciones = await response.json();
    
    const tbody = document.querySelector('#tabla-ubicaciones tbody');
    if (!tbody) return;
    
    tbody.innerHTML = '';
    
    ubicaciones.forEach(u => {
      tbody.innerHTML += `
        <tr>
          <td>${u.nombre}</td>
          <td>${u.tipo}</td>
          <td>${u.lng || 'N/A'}</td>
          <td>${u.lat || 'N/A'}</td>
          <td>${u.nivelRiesgo}</td>
        </tr>
      `;
    });
  } catch (error) {
    console.error('Error cargando ubicaciones:', error);
  }
}

async function mostrarDetalleUbicacion(id) {
  try {
    const response = await fetch(apiBase + '/zonas/' + id);
    const ubicacion = await response.json();
    
    Swal.fire({
      title: 'Ubicación encontrada',
      html: `
        <div style="text-align: left;">
          <b>ID:</b> ${ubicacion.id}<br>
          <b>Nombre:</b> ${ubicacion.nombre}<br>
          <b>Latitud:</b> ${ubicacion.lat}<br>
          <b>Longitud:</b> ${ubicacion.lng}<br>
          <b>Nivel de riesgo:</b> ${ubicacion.nivelRiesgo}<br>
          <b>Personas afectadas:</b> ${ubicacion.personasAfectadas}<br>
          <b>Última actualización:</b> ${new Date(ubicacion.updatedAt).toLocaleString()}<br>
        </div>
      `,
      icon: 'info',
      confirmButtonText: 'OK'
    });
    
    addLog(`${currentUser.nombre} consultó ubicación: ${ubicacion.nombre}`);
  } catch (error) {
    console.error('Error obteniendo ubicación:', error);
    Swal.fire('Error', 'No se pudo cargar la ubicación', 'error');
  }
}

// ============================================
// UTILIDADES
// ============================================
function getBadgeClass(nivelRiesgo) {
  switch (nivelRiesgo) {
    case 'CRITICO':
    case 'ALTO':
      return 'badge-risk-high';
    case 'MEDIO':
      return 'badge-risk-medium';
    default:
      return 'badge-risk-low';
  }
}

function getMarkerColor(nivelRiesgo) {
  switch (nivelRiesgo) {
    case 'CRITICO':
      return '#8B0000';
    case 'ALTO':
      return '#dc3545';
    case 'MEDIO':
      return '#ffc107';
    default:
      return '#198754';
  }
}

function addLog(text) {
  const li = document.createElement('li');
  li.className = 'list-group-item';
  li.textContent = '[' + new Date().toLocaleTimeString() + '] ' + text;
  
  const list = document.getElementById('logActivity');
  if (list) {
    list.prepend(li);
    
    // Mantener solo los últimos 50 logs
    while (list.children.length > 50) {
      list.removeChild(list.lastChild);
    }
  }
}

function applyFilters() {
  const filterRisk = document.getElementById('filterRisk').value;
  
  addLog(`Filtro aplicado por ${currentUser.nombre}: ${filterRisk || 'Todos'}`);
  
  // Aquí implementarías la lógica de filtrado
  Swal.fire('Info', 'Función de filtros en desarrollo', 'info');
}

// ============================================
// WEBSOCKET
// ============================================
function connectWebSocket() {
  const socket = new SockJS('http://localhost:8081/ws');
  stompClient = StompJs.Stomp.over(socket);
  
  stompClient.connect({}, function (frame) {
    console.log('Connected: ' + frame);
    document.getElementById('wsStatus').textContent = 'WS: conectado';
    
    stompClient.subscribe('/topic/evacuaciones', function (message) {
      const payload = JSON.parse(message.body);
      addLog('Evacuación recibida: ' + payload.ubicacion);
    });
  }, function (err) {
    console.error('STOMP error', err);
    document.getElementById('wsStatus').textContent = 'WS: error';
  });
}